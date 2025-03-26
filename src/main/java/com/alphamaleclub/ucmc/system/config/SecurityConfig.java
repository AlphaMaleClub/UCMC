package com.alphamaleclub.ucmc.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf( csrf-> csrf.disable() )
                .formLogin(
                        formLogin ->{
                            formLogin.loginPage("/login");
                        }
                )
                .oauth2Login(Customizer.withDefaults())
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/login")
                            .anonymous()
                        .requestMatchers(("/user/**"))
                            .hasAnyAuthority("USER")
                        .requestMatchers("/admin/**")
                            .hasAnyAuthority("ADMIN")
                        .anyRequest()
                            .authenticated()
                )
                .build();
    }
}
