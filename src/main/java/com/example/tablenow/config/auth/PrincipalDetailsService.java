package com.example.tablenow.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.tablenow.domain.user.User;
import com.example.tablenow.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 로그인 요청 시 호출됨
    // 해당 아이디를 가진 회원을 조회하며, 비밀번호 확인은 시큐리티가 처리해줌
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            return new PrincipalDetails(userEntity);
        } else {
            return new PrincipalDetails(new User()); // 빈 User 엔티티 부여
        }
    }
}
