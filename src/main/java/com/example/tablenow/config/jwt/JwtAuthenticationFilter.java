package com.example.tablenow.config.jwt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.tablenow.config.SecretProperties;
import com.example.tablenow.config.auth.PrincipalDetails;
import com.example.tablenow.web.dto.CMRespDto;
import com.example.tablenow.web.dto.user.UserRepDto.LoginReqDto;
import com.example.tablenow.web.dto.user.UserRespDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

// /login 요청 시 UsernamePasswordAuthenticationFilter 필터 동작
// formLogin().disable()로 설정했기 때문에, 따로 필터 등록 필요!

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    ObjectMapper om = new ObjectMapper();

    // /login 요청 시 호출됨
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter 필터 동작");

        try {
            // 1. request에 있는 JSON 형식의 username과 password를 파싱해 LoginReqDto 객체로 변환
            LoginReqDto loginReqdto = om.readValue(request.getInputStream(), LoginReqDto.class);

            // 2. UsernamePasswordAuthenticationToken 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginReqdto.getUsername(), loginReqdto.getPassword());

            // 3. AuthenticationManager가 생성된 토큰에 대한 인증 진행함
            // 이때 PrincipalDetailsService의 loadUserByUsername() 호출
            // 인증 성공 시 authentication 객체 리턴 (authentication 객체에 PrincipalDetails 담김)
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            return authentication;
        } catch (Exception e) {
            try {
                // 인증 실패 시, 실패 메세지 응답 함수 호출
                failAuthentication(response);
            } catch (IOException e1) {

            }
        }
        return null;
    }

    protected void failAuthentication(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json; charset=utf-8");
        CMRespDto<?> responseEntity = CMRespDto.builder().code(400).message("로그인 실패")
                .response("아이디 또는 비밀번호가 일치하지 않습니다.").build();
        PrintWriter out = response.getWriter();
        out.print(om.writeValueAsString(responseEntity)); // JSON 형식으로 변환
        out.flush();
    }

    // attemptAuthentication() 실행 후 인증 성공 시 호출됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();

        // 4. JWT 토큰 생성
        String jwtToken = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + SecretProperties.JWT_EXPIRATION_TIME))
                .withClaim("id", principal.getUser().getId()).sign(Algorithm.HMAC512(SecretProperties.JWT_SECRET));

        // 5. Header에 토큰과 Body에 회원정보 담아 리턴
        response.addHeader(SecretProperties.JWT_HEADER_STRING, SecretProperties.JWT_TOKEN_PREFIX + jwtToken);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json; charset=utf-8");
        UserRespDto userRespDto = new UserRespDto(principal.getUser());
        userRespDto.hidePassword(); // 비밀번호 숨기기
        CMRespDto<?> responseEntity = CMRespDto.builder().code(200).message("로그인 성공").response(userRespDto).build();
        PrintWriter out = response.getWriter();
        out.print(om.writeValueAsString(responseEntity)); // JSON 형식으로 변환
        out.flush();
    }
}
