package com.alphamaleclub.ucmc.system.config;

import com.alphamaleclub.ucmc.member.services.CustomOAuth2Handler;
import com.alphamaleclub.ucmc.member.services.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2Handler customOAuth2Handler;
    private final MemberServiceImpl memberServiceImpl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf( csrf-> csrf.disable() )
                .formLogin(
                        formLogin ->{
                            formLogin.loginPage("/login");
                        }
                )
                .oauth2Login(oauth2 -> oauth2
                        // 로그인 성공시 처리 핸들러
                        .successHandler(customOAuth2Handler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(memberServiceImpl))
                )
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/login","/oauth2/**")
                            .anonymous()
                        .requestMatchers(("/user/**"))
                            .hasAnyAuthority("USER")
                        .requestMatchers("/admin/**")
                            .hasAnyAuthority("ADMIN")
                        .requestMatchers("/api/auctions/**").permitAll()
                        .anyRequest()
                            .authenticated()
                )
                .build();
    }
}
