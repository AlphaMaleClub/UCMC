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
        String url = request.getRequestURL().toString();
        log.info("[White Filter 로그임] URI: {}", path);
        log.info("[White Filter 로그임] URL: {}", url);

        return (
                path.startsWith("/api/signup") ||
                path.startsWith("/api/access-token") ||
                path.startsWith("/oauth2/initiate") ||
                path.startsWith("/login/oauth2/**") ||
                path.startsWith("/oauth2/authorization/**") ||
                path.startsWith("/api/login") ||
                path.startsWith("/login")
        );

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //어떤 URL 로 여기 백엔드로 들어오게 되었는지 체크.
        String path = request.getRequestURI();
        String url = request.getRequestURL().toString();
        log.info("[Filter 로그임] URI: {}", path);
        log.info("[Filter 로그임] URL: {}", url);

        //토큰 추출하기
        String accessToken;

        try {

            accessToken = tokenManager.extractAccessToken(request);
//            log.info("token extracting success : {}", accessToken);

        } catch (MissingTokenException | NullPointerException e) {

            log.warn(e.getMessage());
            failedProcess();
            filterChain.doFilter(request, response);
            throw new UnauthorizedAccessException("권한없음");

        }


        try {

            successProcess(accessToken, response);

        } catch (MissingTokenException e) {

            log.warn(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{}");
            response.getWriter().flush();
            response.getWriter().close();
            return;

        }

        filterChain.doFilter(request, response);
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
