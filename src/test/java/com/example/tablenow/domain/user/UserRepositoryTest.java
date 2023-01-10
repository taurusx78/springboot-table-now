package com.example.tablenow.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
@DataJpaTest // DB 관련 컴포넌트만 메모리에 로딩 (예. Repository)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @BeforeEach // 각 테스트 시작 전마다 실행
    public void 데이터준비() {
        String username = "tablenow1";
        String password = "tablenow1!";
        String encPassword = encoder.encode(password);
        String name = "김연아";
        String phone = "01011111111";
        String uniqueKey = "uniqueKey1";
        String email = "tablenow1@naver.com";
        userRepository.save(
                User.builder().username(username).password(encPassword).name(name).phone(phone).uniqueKey(uniqueKey)
                        .email(email).build());
    } // 각 테스트가 끝나기 전까지 트랜잭션 유지

    // 가입 여부 조회
    @Test
    public void mCheckJoined() {
        // given (데이터 준비)
        String joinedKey = "uniqueKey1";
        String notJoinedKey = "uniqueKey2";

        // when (테스트 실행)
        int result1 = userRepository.mCheckJoined(joinedKey);
        int result2 = userRepository.mCheckJoined(notJoinedKey);

        // then (검증)
        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(0);
    } // 트랜잭션 종료 및 저장된 데이터 초기화

    // 회원가입
    @Test
    public void save() {
        // given
        String username = "tablenow2";
        String password = "tablenow2@";
        String encPassword = encoder.encode(password);
        String name = "김태리";
        String phone = "01022222222";
        String uniqueKey = "uniqueKey2";
        String email = "tablenow2@naver.com";
        User user = User.builder().username(username).password(encPassword).name(name).phone(phone).uniqueKey(uniqueKey)
                .email(email).build();

        // when
        User userPS = userRepository.save(user);

        // then
        assertThat(userPS.getUsername()).isEqualTo(username);
        assertThat(userPS.getPassword()).isEqualTo(encPassword);
        assertThat(userPS.getName()).isEqualTo(name);
        assertThat(userPS.getPhone()).isEqualTo(phone);
        assertThat(userPS.getUniqueKey()).isEqualTo(uniqueKey);
        assertThat(userPS.getEmail()).isEqualTo(email);
    }

    // 아이디 중복확인
    @Test
    public void mCheckUsername() {
        // given
        String joinedUsername = "tablenow1";
        String notJoinedUsername = "tablenow2";

        // when
        int result1 = userRepository.mCheckUsername(joinedUsername);
        int result2 = userRepository.mCheckUsername(notJoinedUsername);

        // then
        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(0);
    }

    // 아이디로 회원찾기
    @Test
    public void findByUsername() {
        // given
        String joinedUsername = "tablenow1";
        String notJoinedUsername = "tablenow2";

        // when
        User userPS1 = userRepository.findByUsername(joinedUsername);
        User userPS2 = userRepository.findByUsername(notJoinedUsername);

        // then
        assertThat(userPS1.getUsername()).isEqualTo(joinedUsername);
        assertThat(userPS2).isEqualTo(null);
    }

    // 이메일로 아이디 찾기
    @Test
    public void findByEmail() {
        // given
        String username = "tablenow1";
        String[] emails = { "tablenow1@naver.com", "tablenow2@naver.com" };

        for (int i = 0; i < 2; i++) {
            // when
            User userPS = userRepository.findByEmail(emails[i]);

            // then
            if (i == 0) {
                assertThat(userPS.getUsername()).isEqualTo(username);
            } else {
                assertThat(userPS).isEqualTo(null);
            }
        }
    }

    // 휴대폰번호로 아이디 찾기
    @Test
    public void findByPhone() {
        // given
        String username = "tablenow1";
        String[] phones = { "01011111111", "01022222222" };

        for (int i = 0; i < 2; i++) {
            // when
            User userPS = userRepository.findByPhone(phones[i]);

            // then
            if (i == 0) {
                assertThat(userPS.getUsername()).isEqualTo(username);
            } else {
                assertThat(userPS).isEqualTo(null);
            }
        }
    }

    // 이메일로 비밀번호 찾기
    @Test
    public void mCheckUsernameAndEmail() {
        // given
        String[] usernames = { "tablenow1", "tablenow2" };
        String[] emails = { "tablenow1@naver.com", "tablenow2@naver.com" };

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                // when
                int result = userRepository.mCheckUsernameAndEmail(usernames[i], emails[j]);

                // then
                if (i == 0 && j == 0) {
                    assertThat(result).isEqualTo(1);
                } else {
                    assertThat(result).isEqualTo(0);
                }
            }
        }
    }

    // 휴대폰번호로 비밀번호 찾기
    @Test
    public void mCheckUsernameAndPhone() {
        // given
        String[] usernames = { "tablenow1", "tablenow2" };
        String[] phones = { "01011111111", "01022222222" };

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                // when
                int result = userRepository.mCheckUsernameAndPhone(usernames[i], phones[j]);

                // then
                if (i == 0 && j == 0) {
                    assertThat(result).isEqualTo(1);
                } else {
                    assertThat(result).isEqualTo(0);
                }
            }
        }
    }
}
