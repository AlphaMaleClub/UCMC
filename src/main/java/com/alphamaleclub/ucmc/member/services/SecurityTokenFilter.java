package com.alphamaleclub.ucmc.member.services;

import com.alphamaleclub.ucmc.member.domain.Member;
import com.alphamaleclub.ucmc.member.dto.CustomUserDetails;
import com.alphamaleclub.ucmc.member.dto.TokenValueDto;
import com.alphamaleclub.ucmc.system.exception.auth.MissingTokenException;
import com.alphamaleclub.ucmc.system.exception.auth.UnauthorizedAccessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityTokenFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();

        /*
            로그인,회원가입 요청들이 필터를 타면 깔끔하지 않습니다.
            loadUser 나 loadUserByName 을 타려면 여길 거치면 안됩니다.
            그래서 관련한 요청들은 토큰 필터를 타지 않도록
            화이트리스트에 올려 제외시킵니다.
        */

        return (
                path.startsWith("/api/signup") ||
                path.startsWith("/api/access-token") ||
                path.startsWith("/api/login") ||
                path.startsWith("/oauth2/initiate") ||
                path.startsWith("/oauth2/authorization") ||
                path.startsWith("/login") ||
                path.startsWith("/login/oauth2")
        );

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //어떤 URL 로 여기 백엔드로 들어오게 되었는지 체크.
        String url = request.getRequestURL().toString();
        log.info("url = {}", url);


        //토큰 추출하기
        String accessToken;

        try {

            accessToken = tokenManager.extractAccessToken(request);
//            log.info("token extracting success : {}", accessToken);

        } catch (MissingTokenException | NullPointerException e) {

            //헤더에 accessToken이 없는 경우
            log.info("헤더에 accessToken 이 없습니다.");
            failedProcess();
            filterChain.doFilter(request, response);
            return;

        }


        try {

            successProcess(accessToken, response);
            filterChain.doFilter(request, response);

        } catch (MissingTokenException e) {

            // 토큰 내의 값이 유효하지 않은경우.
            // 변조를 의심해야하는 경우 or 토큰이 만료된 경우
            log.warn(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{}");
            response.getWriter().flush();
            response.getWriter().close();

        }


    }

    private void failedProcess() {

        //CustomUserDetails 를 Authentication 객체로 만들기
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ANONYMOUS");

        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, Collections.singletonList(authority));

        //Authentication 에 올리기
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    private void successProcess(String token, HttpServletResponse response) throws IOException, MissingTokenException {

        TokenValueDto tokenDto = tokenManager.extractAccessTokenValue(token);

        //읽은 값으로 멤버 찾기
        Member findMember = tokenManager.tokenDtoToMember(tokenDto);

        //멤버를 CustomUserDetails 로 만들기
        CustomUserDetails customUserDetails = CustomUserDetails.memberToDetails(findMember);

        //CustomUserDetails 를 Authentication 객체로 만들기
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(customUserDetails.getRole());

        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, Collections.singletonList(authority));

        //Authentication 에 올리기
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }


}
