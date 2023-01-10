package com.example.tablenow.web;

import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tablenow.config.auth.PrincipalDetails;
import com.example.tablenow.handler.exception.CustomException;
import com.example.tablenow.service.user.UserService;
import com.example.tablenow.util.Mail;
import com.example.tablenow.web.dto.CMRespDto;
import com.example.tablenow.web.dto.user.UserRepDto.ChangeEmailReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.ChangePwReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.CheckAuthNumberReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.CheckJoinedReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.CheckPwReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.CheckUsernameReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.FindIdReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.FindPwReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.JoinReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.ResetPwReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.SendAuthNumberReqDto;
import com.example.tablenow.web.dto.user.UserRespDto;
import com.example.tablenow.web.dto.user.UserRespDto.FindIdRespDto;
import com.example.tablenow.web.dto.user.UserRespDto.WithdrawalRespDto;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final Mail mail;
    private String randomNumber = "111111"; // 인증번호 (테스트용)

    // 가입여부조회
    @PostMapping("/check/user")
    public ResponseEntity<?> checkJoined(@Valid @RequestBody CheckJoinedReqDto dto, BindingResult bindingResult) {
        int result = userService.가입여부조회(dto); // 있으면 1, 없으면 0
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("가입 여부 조회 완료").response(result).build(),
                HttpStatus.OK);
    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid JoinReqDto dto, BindingResult bindingResult) {
        // *** DB에서 인증번호 조회하도록 수정!
        if (dto.getAuthNumber().equals(randomNumber)) {
            UserRespDto userRespDto = userService.회원가입(dto);
            userRespDto.hidePassword(); // 비밀번호 숨기기
            // 응답 시 userRespDto의 모든 getter 함수가 호출되고 JSON으로 파싱되어 리턴됨
            return new ResponseEntity<>(CMRespDto.builder().code(201).message("회원가입 완료").response(userRespDto).build(),
                    HttpStatus.CREATED);
        } else {
            throw CustomException.builder().code(401).message("인증되지 않는 사용자").response("인증번호가 일치하지 않습니다.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 아이디 중복확인
    // 요청형식: /check?username=아이디
    @GetMapping("/check")
    public ResponseEntity<?> checkUsername(@Valid CheckUsernameReqDto dto, BindingResult bindingResult) {
        int result = userService.아이디중복확인(dto); // 있으면 1, 없으면 0
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("아이디 중복확인 완료").response(result).build(),
                HttpStatus.OK);
    }

    // 인증번호 요청
    // 요청형식: /send?email=이메일 또는 /send?phone=휴대폰번호
    @GetMapping("/send")
    public ResponseEntity<?> sendAuthNumber(@Valid SendAuthNumberReqDto dto, BindingResult bindingResult)
            throws Exception {
        // 이메일 휴대폰번호 모두 Null인 경우
        if (dto.getEmail() == null && dto.getPhone() == null) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").response("이메일 또는 휴대폰번호를 입력해 주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        }

        String method = "이메일";
        if (dto.getEmail() != null) {
            randomNumber = mail.sendEmail(dto.getEmail());
        } else {
            randomNumber = mail.sendSms(dto.getPhone());
            method = "휴대폰번호";
        }
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("인증번호 전송 완료")
                .response("입력한 " + method + "로 인증번호를 전송하였습니다.").build(), HttpStatus.OK);
    }

    // 아이디 찾기
    @PostMapping("/find/id")
    public ResponseEntity<?> findId(@Valid @RequestBody FindIdReqDto dto, BindingResult bindingResult) {
        // *** DB에서 인증번호 조회하도록 수정!
        if (dto.getAuthNumber().equals(randomNumber)) {
            FindIdRespDto findIdRespDto = userService.아이디찾기(dto.getMethod(), dto.getData());
            if (findIdRespDto != null) {
                // 1. 인증 성공 & 회원 존재
                return new ResponseEntity<>(
                        CMRespDto.builder().code(200).message("아이디 찾기 성공").response(findIdRespDto).build(),
                        HttpStatus.OK);
            } else {
                // 2. 인증 성공 & 회원 없음
                return new ResponseEntity<>(
                        CMRespDto.builder().code(200).message("아이디 찾기 실패").build(), HttpStatus.OK);
            }
        } else {
            // 3. 인증 실패
            throw CustomException.builder().code(401).message("인증되지 않는 사용자").response("인증번호가 일치하지 않습니다.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 비밀번호 찾기
    @PostMapping("/find/password")
    public ResponseEntity<?> findPassword(@Valid @RequestBody FindPwReqDto dto, BindingResult bindingResult) {
        // *** DB에서 인증번호 조회하도록 수정!
        if (dto.getAuthNumber().equals(randomNumber)) {
            int result = userService.비밀번호찾기(dto.getMethod(), dto.getData(), dto.getUsername());
            if (result == 1) {
                // 1. 인증 성공 & 회원 존재
                return new ResponseEntity<>(CMRespDto.builder().code(200).message("비밀번호 찾기 성공").build(), HttpStatus.OK);
            } else {
                // 2. 인증 성공 & 회원 없음
                return new ResponseEntity<>(CMRespDto.builder().code(200).message("비밀번호 찾기 실패").build(), HttpStatus.OK);
            }
        } else {
            // 3. 인증 실패
            throw CustomException.builder().code(401).message("인증되지 않는 사용자").response("인증번호가 일치하지 않습니다.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 비밀번호 재설정 (비밀번호 찾기)
    @PutMapping("/reset/password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPwReqDto dto, BindingResult bindingResult) {
        // *** DB에서 인증번호 조회하도록 수정!
        if (dto.getAuthNumber().equals(randomNumber)) {
            UserRespDto userRespDto = userService.비밀번호재설정(dto);
            userRespDto.hidePassword(); // 비밀번호 숨기기
            return new ResponseEntity<>(
                    CMRespDto.builder().code(200).message("비밀번호 재설정 완료").response(userRespDto).build(), HttpStatus.OK);
        } else {
            throw CustomException.builder().code(401).message("인증되지 않는 사용자").response("인증번호가 일치하지 않습니다.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 비밀번호 변경
    @PutMapping("/manager/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePwReqDto dto, BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal) {
        // 비밀번호 정규표현식을 만족하지 않는 경우
        String pwPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=])[a-zA-Z\\d~!@#$%^&*()_+=]{8,20}$";
        if (!Pattern.matches(pwPattern, dto.getCurPassword())) {
            throw CustomException.builder().code(401).message("인증되지 않는 사용자").response("현재 비밀번호가 일치하지 않습니다.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }

        userService.비밀번호변경(principal.getUser().getId(), dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("비밀번호 변경 완료").build(), HttpStatus.OK);
    }

    // 이메일 인증번호 검증 (회원가입, 이메일 변경)
    @PostMapping("/verify/email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody CheckAuthNumberReqDto dto, BindingResult bindingResult) {
        // *** DB에서 인증번호 조회하도록 수정!
        if (dto.getAuthNumber().equals(randomNumber)) {
            return new ResponseEntity<>(CMRespDto.builder().code(200).message("이메일 인증 완료").build(), HttpStatus.OK);
        } else {
            throw CustomException.builder().code(401).message("인증되지 않는 사용자").response("인증번호가 일치하지 않습니다.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 이메일 변경
    @PutMapping("/manager/email")
    public ResponseEntity<?> changeEmail(@Valid @RequestBody ChangeEmailReqDto dto, BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal) {
        // *** DB에서 인증번호 조회하도록 수정!
        if (dto.getAuthNumber().equals(randomNumber)) {
            UserRespDto userRespDto = userService.이메일변경(principal.getUser().getId(), dto);
            userRespDto.hidePassword(); // 비밀번호 숨기기
            return new ResponseEntity<>(
                    CMRespDto.builder().code(200).message("이메일 변경 완료").response(userRespDto).build(), HttpStatus.OK);
        } else {
            throw CustomException.builder().code(401).message("인증되지 않는 사용자").response("인증번호가 일치하지 않습니다.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 회원탈퇴
    // DELETE 요청은 Body가 없기 때문에, 비밀번호 전달을 위해 POST로 요청
    @PostMapping("/manager/withdrawal")
    public ResponseEntity<?> withdrawal(@Valid @RequestBody CheckPwReqDto dto, BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal) {
        // 비밀번호 정규표현식을 만족하지 않는 경우
        String pwPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=])[a-zA-Z\\d~!@#$%^&*()_+=]{8,20}$";
        if (!Pattern.matches(pwPattern, dto.getPassword())) {
            throw CustomException.builder().code(401).message("인증되지 않는 사용자").response("비밀번호가 일치하지 않습니다.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }

        WithdrawalRespDto withdrawalRespDto = userService.회원탈퇴(principal.getUser().getId(), dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("회원탈퇴 완료").response(withdrawalRespDto).build(),
                HttpStatus.OK);
    }
}
