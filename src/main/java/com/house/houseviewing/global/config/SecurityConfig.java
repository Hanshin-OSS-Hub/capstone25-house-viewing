package com.house.houseviewing.global.config;

import com.house.houseviewing.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Security를 위한 Config 선언
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .csrf((AbstractHttpConfigurer::disable))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic((AbstractHttpConfigurer::disable))
                .sessionManagement((session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)))
                .authorizeHttpRequests((auth -> auth
                        .requestMatchers(
                                "/users/login",
                                "/users/register",
                                "/swagger-ui/**")
                        .permitAll()
                        .anyRequest().authenticated()))
                .addFilterBefore(new )

//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 폼 끄기
//                .httpBasic(AbstractHttpConfigurer::disable) // Basic 인증 끄기
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 미사용
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll() // ⭐ 모든 경로(anyRequest)를 로그인 없이 허용(permitAll)
//                );
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/users/register", "/users/login", "/users/find-id", "/users/password/verify", "/users/password/reset").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
