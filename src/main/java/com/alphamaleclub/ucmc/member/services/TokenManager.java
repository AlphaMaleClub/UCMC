package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.Repositorty.RefreshTokenRepository;
import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.domain.RefreshToken;
import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.member.dto.TokenPair;
import com.alphamaleclub.ucmc.system.exception.ExceptionMessage;
import com.alphamaleclub.ucmc.system.exception.auth.InvalidReIssueRequestException;
import com.alphamaleclub.ucmc.system.exception.auth.MissingTokenException;
import com.alphamaleclub.ucmc.system.exception.member.UserNotFoundException;
import com.alphamaleclub.ucmc.system.util.SecurityUtil;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.security.PublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                .claim("role", user.getAuthorities()) //
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

    public boolean validateWithKey(String token){

        if(validateToken(token,keyManager.getPublicKey())){
            return true;
        }else if(validateToken(token, keyManager.getPublicKey("previous"))) {
            return true;
        }
        log.warn("해당 토큰이 문제를 일으켰습니다. = {}", token);
        return false;

    }

    private String extractRefreshToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new MissingTokenException(ExceptionMessage.Auth.TOKEN_NOT_FOUND));

    }

    private boolean validateToken(String token, PublicKey publicKey) {

        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token); // 본문 반환함.
            return true;
        }catch (JwtException e) {
            log.warn("파싱에 실패했습니다. 토큰이 만료됐거나 키 오류입니다. = {}", e.getMessage());
        }catch (IllegalArgumentException e) {
            log.warn("JWT 본문 스트링이 비어있습니다. = {}", e.getMessage());
        }catch (Exception e){
            log.warn("알 수 없는 토큰에러입니다. = {}", e.getMessage());
        }
        return false;

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
            throw new UserNotFoundException(ExceptionMessage.MemberAuth.MEMBER_NOT_FOUND);
        }

    }

    public void expireRefreshToken() {

        Member member = memberService.getMemberById(SecurityUtil.getCurrentMemberId());

        member.getRefreshTokens().stream()
                .filter(refToken -> (!refToken.isExpired()))
                .map(refToken -> {
                        refToken.setExpired(true);
                        return refreshTokenRepository.save(refToken);
                })
                .forEach(refToken ->{
                    log.info("해당 RefreshToken 이 Expired 처리 되었습니다. \n Value = {}", refToken.getToken());
                });

    }

    public void refreshTokenReIssue(HttpServletRequest request) {

        String token = extractRefreshToken(request);

        Member targetMember = memberService.getMemberById(SecurityUtil.getCurrentMemberId());

        targetMember.getRefreshTokens().stream()
                .filter(refToken -> (!refToken.isExpired()))
                .findFirst();




        if(!validateWithKey(token) || ){
            throw new InvalidReIssueRequestException(ExceptionMessage.Auth.INVALID_REISSUE_REQUEST)
        }

    }

}
