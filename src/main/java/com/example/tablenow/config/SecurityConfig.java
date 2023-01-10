package com.example.tablenow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.example.tablenow.config.jwt.JwtAuthenticationFilter;
import com.example.tablenow.config.jwt.JwtAuthorizationFilter;
import com.example.tablenow.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    // JwtAuthenticationFilter, JwtAuthorizationFilter에서 사용할 AuthenticationManager를
    // 빈으로 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // CSRF 토큰 해제
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용하지 않음 (JWT 기본 설정)
                .and()
                .formLogin().disable() // /login 요청 시 시큐리티가 로그인 처리를 하지 않도록 설정 (JWT 기본 설정)
                .httpBasic().disable() // JWT 토큰을 이용한 인증 방식 사용 (Http Bearer)
                .addFilter(new JwtAuthenticationFilter(authenticationManager(new AuthenticationConfiguration())))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(new AuthenticationConfiguration()),
                        userRepository))
                .authorizeRequests()
                // .antMatchers("/manager/**").access("hasRole('ROLE_MANAGER')")
                .antMatchers("/manager/**").authenticated()
                .anyRequest().permitAll();

        // *** H2 콘솔 사용을 위해 추가 (배포 시 삭제하기!)
        http.headers().frameOptions().disable();

        return http.build();
    }
}
