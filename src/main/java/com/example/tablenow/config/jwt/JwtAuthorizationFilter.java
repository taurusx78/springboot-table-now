package com.example.tablenow.config.jwt;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.tablenow.config.SecretProperties;
import com.example.tablenow.config.auth.PrincipalDetails;
import com.example.tablenow.domain.user.User;
import com.example.tablenow.domain.user.UserRepository;

// 모든 요청에 대해 해당 필터 실행됨

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("JwtAuthorizationFilter 실행됨");

        // 1. Header에 Authorization 속성이 있는지 확인
        String token = request.getHeader(SecretProperties.JWT_HEADER_STRING);

        // 토큰이 없는 경우 return
        if (token == null || !token.startsWith(SecretProperties.JWT_TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        token = token.replace(SecretProperties.JWT_TOKEN_PREFIX, "");

        // 2. 토큰을 검증하여 인증된 사용자인지 확인
        try {
            DecodedJWT decodedJwt = JWT.require(Algorithm.HMAC512(SecretProperties.JWT_SECRET)).build().verify(token);
            Long id = decodedJwt.getClaim("id").asLong();
            Optional<User> userOP = userRepository.findById(id);

            // 해당 사용자가 존재하지 않는 경우
            if (userOP.isEmpty()) {
                chain.doFilter(request, response);
                return;
            }

            PrincipalDetails principalDetails = new PrincipalDetails(userOP.get());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails, // 컨트롤러에서 @AuthenticationPrincipal를 통해 유저 정보 사용 예정
                    null, // 비밀번호는 알 필요없음
                    principalDetails.getAuthorities());

            // 강제로 시큐리티의 세션에 접근하여 값 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {

        }

        chain.doFilter(request, response);
    }
}
