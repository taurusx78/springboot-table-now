// package com.example.tablenow.service;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Spy;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.test.context.ActiveProfiles;

// import com.example.tablenow.domain.store.StoreRepository;
// import com.example.tablenow.domain.user.User;
// import com.example.tablenow.domain.user.UserRepository;
// import com.example.tablenow.service.store.StoreService1;
// import com.example.tablenow.service.user.UserService;
// import com.example.tablenow.web.dto.user.ChangePwReqDto;
// import com.example.tablenow.web.dto.user.FindIdRespDto;
// import com.example.tablenow.web.dto.user.JoinReqDto;
// import com.example.tablenow.web.dto.user.UserRespDto;
// import com.example.tablenow.web.dto.user.WithdrawalRespDto;

// // Repository를 제외한 Service만 테스트하기 위해 가짜데이터 생성
// // @Mock: 진짜 객체를 추상화된 가짜 객체로 만들어서 Mockito 환경에 주입
// // @Spy: 진짜 객체를 만들어서 Mockito 환경에 주입

// @ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
// @ExtendWith(MockitoExtension.class) // 가짜 메모리 환경 생성
// public class UserServiceTest {

//     @InjectMocks // 가짜데이터 주입 받음
//     private UserService userService;

//     @Mock // 가짜 객체 생성
//     private UserRepository userRepository;

//     @Mock
//     private StoreRepository storeRepository;

//     @Mock
//     private StoreService1 storeService1;

//     @Spy
//     private BCryptPasswordEncoder encoder;

//     @Test
//     public void 가입여부조회() {
//         // given
//         String uniqueKey = "uniqueKey1";

//         // stub (가설) : userRepository의 mCheckJoined() 실행 시 0 리턴
//         when(userRepository.mCheckJoined(uniqueKey)).thenReturn(0);

//         // when
//         int result = userService.가입여부조회(uniqueKey);

//         // then
//         assertThat(result).isEqualTo(0);
//     }

//     @Test
//     public void 회원가입() {
//         // given
//         String rawPassword = "tablenow1!";
//         String encPassword = encoder.encode(rawPassword); // 비밀번호 테스트를 위해 미리 해쉬해서 전달
//         JoinReqDto dto = new JoinReqDto("tablenow1", encPassword, "김연아", "01011111111", "uniqueKey1",
//                 "tablenow1@naver.com");

//         // stub: userRepository의 save() 실행 시 dto.toEntity() 리턴
//         when(userRepository.save(any())).thenReturn(dto.toEntity());

//         // when
//         UserRespDto userRespDto = userService.회원가입(dto);

//         // then
//         assertThat(userRespDto.getUsername()).isEqualTo(dto.getUsername());
//         assertTrue(encoder.matches(rawPassword, userRespDto.getPassword()));
//         assertThat(userRespDto.getName()).isEqualTo(dto.getName());
//         assertThat(userRespDto.getPhone()).isEqualTo(dto.getPhone());
//         assertThat(userRespDto.getEmail()).isEqualTo(dto.getEmail());
//     }

//     @Test
//     public void 아이디중복확인() {
//         // given
//         String username = "tablenow1";

//         // stub: userRepository의 mCheckUsername() 실행 시 0 리턴
//         when(userRepository.mCheckUsername(username)).thenReturn(0);

//         // when
//         int result = userService.아이디중복확인(username);

//         // then
//         assertThat(result).isEqualTo(0);
//     }

//     @Test
//     public void 아이디찾기() {
//         // given
//         String email = "tablenow1@naver.com";
//         String phone = "01011111111";
//         User user = User.builder().username("tablenow1").email(email).phone(phone)
//                 .createdDate(LocalDateTime.of(2022, 1, 1, 0, 0, 0))
//                 .build();

//         // stub
//         // 1. userRepository의 findByEmail 실행 시 user 리턴
//         when(userRepository.findByEmail(email)).thenReturn(user);
//         // 2. userRepository의 findByPhone 실행 시 user 리턴
//         when(userRepository.findByPhone(phone)).thenReturn(user);

//         // when
//         FindIdRespDto FindIdRespDto1 = userService.아이디찾기("email", email);
//         FindIdRespDto FindIdRespDto2 = userService.아이디찾기("phone", phone);

//         // then
//         assertThat(FindIdRespDto1.getUsername()).isEqualTo(user.getUsername());
//         assertThat(FindIdRespDto1.getCreatedDate()).isEqualTo(user.getCreatedDate().toString());
//         assertThat(FindIdRespDto2.getUsername()).isEqualTo(user.getUsername());
//         assertThat(FindIdRespDto2.getCreatedDate()).isEqualTo(user.getCreatedDate().toString());
//     }

//     @Test
//     public void 비밀번호찾기() {
//         // given
//         String username = "tablenow1";
//         String email = "tablenow1@naver.com";
//         String phone = "01011111111";

//         // stub
//         // 1. userRepository의 mCheckUsernameAndEmail 실행 시 1 리턴
//         when(userRepository.mCheckUsernameAndEmail(username, email)).thenReturn(1);
//         // 2. userRepository의 mCheckUsernameAndPhone 실행 시 1 리턴
//         when(userRepository.mCheckUsernameAndPhone(username, phone)).thenReturn(1);

//         // when
//         int result1 = userService.비밀번호찾기("email", email, username);
//         int result2 = userService.비밀번호찾기("phone", phone, username);

//         // then
//         assertThat(result1).isEqualTo(1);
//         assertThat(result2).isEqualTo(1);
//     }

//     @Test
//     public void 비밀번호재설정() {
//         // given
//         String username = "tablenow1";
//         String newPassword = "tablenow1!";
//         User user = User.builder().username(username).build();

//         // stub: userRepository의 findByUsername() 실행 시 user 리턴
//         when(userRepository.findByUsername(username)).thenReturn(user);

//         // when
//         UserRespDto userRespDto = userService.비밀번호재설정(username, newPassword);

//         // then
//         assertThat(userRespDto.getUsername()).isEqualTo(username);
//         assertTrue(encoder.matches(newPassword, userRespDto.getPassword()));
//     }

//     @Test
//     public void 비밀번호변경() {
//         // given
//         Long id = 1L;
//         String curPassword = "tablenow1!";
//         String notCurPassword = "tablenow1@";
//         User user = User.builder().id(id).password(encoder.encode(curPassword)).build();

//         ChangePwReqDto changePwReqDto1 = new ChangePwReqDto(curPassword, "tablenow2@");
//         ChangePwReqDto changePwReqDto2 = new ChangePwReqDto(notCurPassword, "tablenow2@");

//         // stub: userRepository의 findById() 실행 시 user 리턴
//         when(userRepository.findById(id)).thenReturn(Optional.of(user));

//         // when
//         int result1 = userService.비밀번호변경(id, changePwReqDto1);
//         int result2 = userService.비밀번호변경(id, changePwReqDto2);

//         // then
//         assertThat(result1).isEqualTo(1); // 변경 성공
//         assertThat(result2).isEqualTo(-1); // 변경 실패
//     }

//     @Test
//     public void 이메일변경() {
//         // given
//         Long id = 1L;
//         String newEmail = "tablenow2@naver.com";
//         User user = User.builder().id(id).email("tablenow1@naver.com").build();

//         // stub: userRepository의 findById() 실행 시 user 리턴
//         when(userRepository.findById(id)).thenReturn(Optional.of(user));

//         // when
//         UserRespDto userRespDto = userService.이메일변경(id, newEmail);

//         // then
//         assertThat(userRespDto.getEmail()).isEqualTo(newEmail);
//     }

//     @Test
//     public void 비밀번호확인() {
//         // given
//         Long id = 1L;
//         String curPassword = "tablenow1!";
//         String notCurPassword = "tablenow1@";
//         User user = User.builder().id(id).password(encoder.encode(curPassword)).build();

//         // stub: userRepository의 findById() 실행 시 user 리턴
//         when(userRepository.findById(id)).thenReturn(Optional.of(user));

//         // when
//         boolean result1 = userService.비밀번호확인(id, curPassword);
//         boolean result2 = userService.비밀번호확인(id, notCurPassword);

//         // then
//         assertThat(result1).isEqualTo(true); // 비밀번호 일치
//         assertThat(result2).isEqualTo(false); // 비밀번호 불일치
//     }

//     @Test
//     public void 회원탈퇴() {
//         // given
//         Long id = 1L;
//         String curPassword = "tablenow1!";
//         String notCurPassword = "tablenow1@";
        
//         // stub
//         // 1. userRepository의 findById() 실행 시 user 리턴
//         User user = User.builder().id(id).name("김지은").password(encoder.encode(curPassword)).build();
//         when(userRepository.findById(id)).thenReturn(Optional.of(user));
//         // 2. storeRepository의 mFindAllIdByUserId() 실행 시 storeIdList 리턴
//         List<Long> storeIdList = List.of(1L, 2L, 3L);
//         when(storeRepository.mFindAllIdByUserId(id)).thenReturn(storeIdList);

//         // when
//         WithdrawalRespDto withdrawalRespDto1 = userService.회원탈퇴(id, curPassword);
//         WithdrawalRespDto withdrawalRespDto2 = userService.회원탈퇴(id, notCurPassword);

//         // then
//         // 탈퇴 성공
//         assertThat(withdrawalRespDto1.getName()).isEqualTo(user.getName());
//         assertThat(withdrawalRespDto1.getDeletedStoreCount()).isEqualTo(storeIdList.size());
//         // 탈퇴 실패
//         assertThat(withdrawalRespDto2).isNull();
//     }
// }
