package com.alphamaleclub.ucmc.system.config;

import com.alphamaleclub.ucmc.member.services.CustomSuccessHandler;
import com.alphamaleclub.ucmc.member.services.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomSuccessHandler customSuccessHandler;
    private final MemberServiceImpl memberServiceImpl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf( csrf-> csrf.disable() )
                .formLogin(formLogin ->formLogin
                        .loginProcessingUrl("/login")
                        .successHandler(customSuccessHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        // 로그인 성공시 처리 핸들러
                        .successHandler(customSuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(memberServiceImpl))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID","accessToken","refreshToken")
                )
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/login","/oauth2/**","/api/signup")
                            .anonymous()
                        .requestMatchers(("/logout"))
                            .hasAnyAuthority("MEMBER", "ADMIN")
                        .requestMatchers("/admin/**")
                            .hasAnyAuthority("ADMIN")
                        .requestMatchers("/api/auctions/**").permitAll()
                        .anyRequest()
                            .authenticated()
                )
                .build();
    }
}
