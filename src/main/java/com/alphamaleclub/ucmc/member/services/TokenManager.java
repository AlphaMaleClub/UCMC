package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.Repositorty.RefreshTokenRepository;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.domain.RefreshToken;
import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.member.dto.TokenPair;
import com.alphamaleclub.ucmc.member.dto.TokenValueDto;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidReIssueRequestException;
import com.alphamaleclub.ucmc.system.exception.auth.MissingTokenException;
import com.alphamaleclub.ucmc.system.exception.member.UserNotFoundException;
import com.alphamaleclub.ucmc.system.util.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.security.PublicKey;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TokenManager {

    @Value("${jwt.token.expiration-time.access}")
    private int ACCESS_TOKEN_VALIDITY_TIME;

    @Value("${jwt.token.expiration-time.refresh}" )
    private int REFRESH_TOKEN_VALIDITY_TIME;

    private Long ACCESS_TOKEN_VALIDITY_IN_MS;
    private Long REFRESH_TOKEN_VALIDITY_IN_MS;

    private final KeyManager keyManager;
    private final MemberService memberService;
    private final CookiesManager cookiesManager;
    private final RefreshTokenRepository refreshTokenRepository;


    @PostConstruct
    public void init(){

        ACCESS_TOKEN_VALIDITY_IN_MS = ACCESS_TOKEN_VALIDITY_TIME * 1000L * 60L;
        REFRESH_TOKEN_VALIDITY_IN_MS = REFRESH_TOKEN_VALIDITY_TIME * 1000L * 60L;

    }

    public String generateAccessToken(CustomUserDetails user) {
        return Jwts.builder()
                .subject(user.getUserId().toString()) //제목처럼 쓰임
                .header()
                    .add("kid",keyManager.getKid())
                    .and()
                .claim("role", user.getRole()) //
                .claim("name", user.getNickname())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + ACCESS_TOKEN_VALIDITY_IN_MS))
                .signWith(keyManager.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(CustomUserDetails user) {
        return Jwts.builder()
                .subject(user.getUserId().toString()) //유저번호가 JWT 헤더에 있음.
                .header()
                    .add("kid",keyManager.getKid())
                    .and()
                .claim("name", user.getNickname())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + REFRESH_TOKEN_VALIDITY_IN_MS))
                .signWith(keyManager.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public TokenPair generateTokenPair(CustomUserDetails user) {
        return TokenPair.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(generateRefreshToken(user))
                .build();
    }

    public Jws<Claims> validateWithKey(String token) throws MissingTokenException {
        return validateToken(token, keyManager.getPublicKey())
                .or(() -> validateToken(token, keyManager.getPublicKey("previous")))
                .orElseThrow(() -> new MissingTokenException(ExceptionMessage.Auth.TOKEN_IS_NOT_VALID));
    }


    public String extractAccessToken(HttpServletRequest request) throws MissingTokenException {

        return Optional.of(request.getHeader("Authorization"))
                .filter(tokenHeader -> tokenHeader.startsWith("Bearer "))
                .map(tokenHeader -> tokenHeader.substring("Bearer ".length()))
                .orElseThrow(() -> new MissingTokenException(ExceptionMessage.Auth.ACCESS_TOKEN_IS_NOT_VALID));

    }

    private String extractRefreshToken(HttpServletRequest request) throws MissingTokenException, NullPointerException {

        Cookie[] cookies = request.getCookies();

//        log.info("extractRefreshToken cookies: {}", (cookies == null) ? "null" : Arrays.toString(cookies));

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new MissingTokenException(ExceptionMessage.Auth.TOKEN_NOT_FOUND + "TokenKey : Refresh Token "));

    }

    private Optional<Jws<Claims>> parsingToken(String token, PublicKey publicKey) {
        try {
            return Optional.of(Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token));// 본문 반환함.
        }catch (JwtException e) {
            log.warn("파싱에 실패했습니다. 토큰이 만료됐거나 키 오류입니다. = {}", e.getMessage());
        }catch (IllegalArgumentException e) {
            log.warn("JWT 본문 스트링이 비어있습니다. = {}", e.getMessage());
        }catch (Exception e){
            log.warn("알 수 없는 토큰에러입니다. = {}", e.getMessage());
        }
        return Optional.empty();
    }

    private Optional<Jws<Claims>> validateToken(String token, PublicKey publicKey) {
        return parsingToken(token, publicKey);
    }

    public void saveRefreshToken(String refreshTokenString){

        try {
            refreshTokenRepository.save(
                    RefreshToken.builder()
                            .token(refreshTokenString)
                            .member(memberService.getMemberById(SecurityUtil.getCurrentMemberId()))
                            .build()
            );
        }catch (NullPointerException e){
            log.warn("로그인되지 않은 사용자가 RefreshToken 을 발급을 시도했습니다.");
            throw new UserNotFoundException(ExceptionMessage.Member.MEMBER_NOT_FOUND);
        }

    }

    public void expireRefreshToken(Member member) {

        member.getRefreshTokens().stream()
                .filter(refToken -> (!refToken.isExpired()))
                .map(refToken -> {
                        refToken.setExpired(true);
                        return refreshTokenRepository.save(refToken);
                })
                .forEach(refToken ->{
                    log.info("해당 RefreshToken 이 Expired 처리 되었습니다. Value = {}", refToken.getToken().substring(5,30));
                });

    }

    public String accessTokenReIssue(HttpServletRequest request) {

        String refreshToken;

        try {
            refreshToken = extractRefreshToken(request);
        } catch (MissingTokenException e) { //리프레시 토큰을 쿠키에서 못꺼내는 경우
            log.warn(e.getMessage());
            return null;
        }

        long getMemberId;

        try{
            getMemberId = Long.parseLong(validateWithKey(refreshToken).getPayload().getSubject());
        }catch (MissingTokenException e){ // 토큰이 Expired 됐거나 발급된 리프레시 토큰이 하나 복수개인 경우 이상감지
            throw new InvalidReIssueRequestException(ExceptionMessage.Auth.INVALID_REISSUE_REQUEST);
        }

        //accessTokenReIssue 는 FilterChain 을 통과하지 못하기 때문에 컨텍스트에 있는걸 가져오면 안된다.
        Member targetMember = memberService.getMemberById(getMemberId);

        List<RefreshToken> refreshTokens = targetMember.getRefreshTokens().stream()
                .filter(refToken -> (!refToken.isExpired()))
                .toList();


        if(refreshTokens.size() != 1){
            refreshTokens.forEach(refToken -> {
                log.info("TokenValue = {}", refToken.getToken());
            });
            throw new InvalidReIssueRequestException(ExceptionMessage.Auth.MULTIPLE_ISSUED_REFRESH_TOKENS);
        }

//        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails user = CustomUserDetails.memberToDetails(targetMember);

        return generateAccessToken(user);

    }

    public TokenValueDto extractAccessTokenValue(String token) throws MissingTokenException{

        Jws<Claims> resultBody = validateWithKey(token);

        Claims claims = resultBody.getPayload();

        return TokenValueDto.builder()
                .id(claims.getSubject())
                .nickname(claims.get("name", String.class))
                .role(claims.get("role", String.class))
                .build();
    }

    public Member tokenDtoToMember (TokenValueDto tokenValueDto){

        return memberService.getMemberById(Long.valueOf(tokenValueDto.getId()));

    }

}
