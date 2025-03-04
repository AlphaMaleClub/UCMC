package com.alphamaleclub.ucmc.system.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

//    private final MemberServiceImpl memberServiceImpl;
//    // 로그인 성공 후 JWT 발급 & 리다이렉트
//    private final OAuth2SuccessHandler oAuth2SuccessHandler;
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        return http
//                // CSRF / CORS / httpBasic / formLogin / session 정책등
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.configure(http))
//                .httpBasic(httpBasic -> httpBasic.disable())
//                .formLogin(form -> form.disable())
//                // 세션은 전혀 사용하지않고 OAuth 인증 시에도 JWT로 인증
//                // OAuth 인증은 어차피 리디렉션으로 처리됨
//                // SuccessHandler 에서 토큰을 바로 발급해줌.
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//
//                // OAuth2 리다이렉트 방식
//                .oauth2Login(oauth2 -> oauth2
//                        // 로그인 성공시 처리 핸들러
//                        .successHandler(oAuth2SuccessHandler)
//                        .userInfoEndpoint(userInfo -> userInfo.userService(memberServiceImpl))
//                )
//
//                // 인증, 권한 설정
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/ws", "/ws/**", "/ws/info", "/actuator/health")
//                        .permitAll()
//                        .requestMatchers("/api/members/**")
//                        .hasAnyAuthority("ADMIN", "MEMBER")
//                        .requestMatchers("/api/admin/**")
//                        .hasAuthority("ADMIN")
//                        .requestMatchers("/chat/**","/publish/**")
//                        .permitAll()
//                        .anyRequest()
//                        .permitAll()
//                )
//
//                // 인증 실패 시 401코드 JSON 반환
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint((req, resp, ex) -> {
//                            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                            resp.setContentType("application/json");
//                            resp.getWriter().write("{\"error\": \"Unauthorized\"}");
//                        })
//                )
//
//                // JWT 필터를 추가
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//
//    }

}
