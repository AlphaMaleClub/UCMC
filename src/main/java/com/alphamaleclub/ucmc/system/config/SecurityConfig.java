package com.alphamaleclub.ucmc.system.config;

import com.alphamaleclub.ucmc.member.services.CustomSuccessHandler;
import com.alphamaleclub.ucmc.member.services.MemberServiceImpl;
import com.alphamaleclub.ucmc.member.services.SecurityTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityTokenFilter securityTokenFilter;
    private final CustomSuccessHandler customSuccessHandler;
    private final MemberServiceImpl memberServiceImpl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // 시큐리티 레벨에서도 cors허용
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //JSESSION 제거
                .csrf(csrf-> csrf.disable() )
                .formLogin(formLogin ->formLogin
                        .loginProcessingUrl("/api/login")
                        .permitAll()
                        .successHandler(customSuccessHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        // 로그인 성공시 처리 핸들러
                        .successHandler(customSuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(memberServiceImpl))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID","accessToken","refreshToken")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/initiate","/api/signup","/api/access-token")
                            .permitAll()
                        .requestMatchers(("/logout"))
                            .hasAnyAuthority("MEMBER", "ADMIN")
                        .requestMatchers("/admin/**")
                            .hasAnyAuthority("ADMIN")
                        .requestMatchers("/api/auctions/**")
                            .permitAll()
                        .requestMatchers("/api/trade-posts/**")
                        .permitAll()
                        .anyRequest()
                            .authenticated()
                )
                .addFilterBefore(securityTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean //시큐티리 레벨에서 사용될 Cors 설정을 여기에 쓸 수 있다.
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

}