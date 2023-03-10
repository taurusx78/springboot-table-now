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
    private String randomNumber = "111111"; // ???????????? (????????????)

    // ??????????????????
    @PostMapping("/check/user")
    public ResponseEntity<?> checkJoined(@Valid @RequestBody CheckJoinedReqDto dto, BindingResult bindingResult) {
        int result = userService.??????????????????(dto); // ????????? 1, ????????? 0
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("?????? ?????? ?????? ??????").response(result).build(),
                HttpStatus.OK);
    }

    // ????????????
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid JoinReqDto dto, BindingResult bindingResult) {
        // *** DB?????? ???????????? ??????????????? ??????!
        if (dto.getAuthNumber().equals(randomNumber)) {
            UserRespDto userRespDto = userService.????????????(dto);
            userRespDto.hidePassword(); // ???????????? ?????????
            // ?????? ??? userRespDto??? ?????? getter ????????? ???????????? JSON?????? ???????????? ?????????
            return new ResponseEntity<>(CMRespDto.builder().code(201).message("???????????? ??????").response(userRespDto).build(),
                    HttpStatus.CREATED);
        } else {
            throw CustomException.builder().code(401).message("???????????? ?????? ?????????").response("??????????????? ???????????? ????????????.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ????????? ????????????
    // ????????????: /check?username=?????????
    @GetMapping("/check")
    public ResponseEntity<?> checkUsername(@Valid CheckUsernameReqDto dto, BindingResult bindingResult) {
        int result = userService.?????????????????????(dto); // ????????? 1, ????????? 0
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("????????? ???????????? ??????").response(result).build(),
                HttpStatus.OK);
    }

    // ???????????? ??????
    // ????????????: /send?email=????????? ?????? /send?phone=???????????????
    @GetMapping("/send")
    public ResponseEntity<?> sendAuthNumber(@Valid SendAuthNumberReqDto dto, BindingResult bindingResult)
            throws Exception {
        // ????????? ??????????????? ?????? Null??? ??????
        if (dto.getEmail() == null && dto.getPhone() == null) {
            throw CustomException.builder().code(400).message("????????? ?????? ??????").response("????????? ?????? ?????????????????? ????????? ?????????.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        }

        String method = "?????????";
        if (dto.getEmail() != null) {
            randomNumber = mail.sendEmail(dto.getEmail());
        } else {
            randomNumber = mail.sendSms(dto.getPhone());
            method = "???????????????";
        }
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("???????????? ?????? ??????")
                .response("????????? " + method + "??? ??????????????? ?????????????????????.").build(), HttpStatus.OK);
    }

    // ????????? ??????
    @PostMapping("/find/id")
    public ResponseEntity<?> findId(@Valid @RequestBody FindIdReqDto dto, BindingResult bindingResult) {
        // *** DB?????? ???????????? ??????????????? ??????!
        if (dto.getAuthNumber().equals(randomNumber)) {
            FindIdRespDto findIdRespDto = userService.???????????????(dto.getMethod(), dto.getData());
            if (findIdRespDto != null) {
                // 1. ?????? ?????? & ?????? ??????
                return new ResponseEntity<>(
                        CMRespDto.builder().code(200).message("????????? ?????? ??????").response(findIdRespDto).build(),
                        HttpStatus.OK);
            } else {
                // 2. ?????? ?????? & ?????? ??????
                return new ResponseEntity<>(
                        CMRespDto.builder().code(200).message("????????? ?????? ??????").build(), HttpStatus.OK);
            }
        } else {
            // 3. ?????? ??????
            throw CustomException.builder().code(401).message("???????????? ?????? ?????????").response("??????????????? ???????????? ????????????.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ???????????? ??????
    @PostMapping("/find/password")
    public ResponseEntity<?> findPassword(@Valid @RequestBody FindPwReqDto dto, BindingResult bindingResult) {
        // *** DB?????? ???????????? ??????????????? ??????!
        if (dto.getAuthNumber().equals(randomNumber)) {
            int result = userService.??????????????????(dto.getMethod(), dto.getData(), dto.getUsername());
            if (result == 1) {
                // 1. ?????? ?????? & ?????? ??????
                return new ResponseEntity<>(CMRespDto.builder().code(200).message("???????????? ?????? ??????").build(), HttpStatus.OK);
            } else {
                // 2. ?????? ?????? & ?????? ??????
                return new ResponseEntity<>(CMRespDto.builder().code(200).message("???????????? ?????? ??????").build(), HttpStatus.OK);
            }
        } else {
            // 3. ?????? ??????
            throw CustomException.builder().code(401).message("???????????? ?????? ?????????").response("??????????????? ???????????? ????????????.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ???????????? ????????? (???????????? ??????)
    @PutMapping("/reset/password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPwReqDto dto, BindingResult bindingResult) {
        // *** DB?????? ???????????? ??????????????? ??????!
        if (dto.getAuthNumber().equals(randomNumber)) {
            UserRespDto userRespDto = userService.?????????????????????(dto);
            userRespDto.hidePassword(); // ???????????? ?????????
            return new ResponseEntity<>(
                    CMRespDto.builder().code(200).message("???????????? ????????? ??????").response(userRespDto).build(), HttpStatus.OK);
        } else {
            throw CustomException.builder().code(401).message("???????????? ?????? ?????????").response("??????????????? ???????????? ????????????.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ???????????? ??????
    @PutMapping("/manager/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePwReqDto dto, BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal) {
        // ???????????? ?????????????????? ???????????? ?????? ??????
        String pwPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=])[a-zA-Z\\d~!@#$%^&*()_+=]{8,20}$";
        if (!Pattern.matches(pwPattern, dto.getCurPassword())) {
            throw CustomException.builder().code(401).message("???????????? ?????? ?????????").response("?????? ??????????????? ???????????? ????????????.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }

        userService.??????????????????(principal.getUser().getId(), dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("???????????? ?????? ??????").build(), HttpStatus.OK);
    }

    // ????????? ???????????? ?????? (????????????, ????????? ??????)
    @PostMapping("/verify/email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody CheckAuthNumberReqDto dto, BindingResult bindingResult) {
        // *** DB?????? ???????????? ??????????????? ??????!
        if (dto.getAuthNumber().equals(randomNumber)) {
            return new ResponseEntity<>(CMRespDto.builder().code(200).message("????????? ?????? ??????").build(), HttpStatus.OK);
        } else {
            throw CustomException.builder().code(401).message("???????????? ?????? ?????????").response("??????????????? ???????????? ????????????.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ????????? ??????
    @PutMapping("/manager/email")
    public ResponseEntity<?> changeEmail(@Valid @RequestBody ChangeEmailReqDto dto, BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal) {
        // *** DB?????? ???????????? ??????????????? ??????!
        if (dto.getAuthNumber().equals(randomNumber)) {
            UserRespDto userRespDto = userService.???????????????(principal.getUser().getId(), dto);
            userRespDto.hidePassword(); // ???????????? ?????????
            return new ResponseEntity<>(
                    CMRespDto.builder().code(200).message("????????? ?????? ??????").response(userRespDto).build(), HttpStatus.OK);
        } else {
            throw CustomException.builder().code(401).message("???????????? ?????? ?????????").response("??????????????? ???????????? ????????????.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ????????????
    // DELETE ????????? Body??? ?????? ?????????, ???????????? ????????? ?????? POST??? ??????
    @PostMapping("/manager/withdrawal")
    public ResponseEntity<?> withdrawal(@Valid @RequestBody CheckPwReqDto dto, BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal) {
        // ???????????? ?????????????????? ???????????? ?????? ??????
        String pwPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=])[a-zA-Z\\d~!@#$%^&*()_+=]{8,20}$";
        if (!Pattern.matches(pwPattern, dto.getPassword())) {
            throw CustomException.builder().code(401).message("???????????? ?????? ?????????").response("??????????????? ???????????? ????????????.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }

        WithdrawalRespDto withdrawalRespDto = userService.????????????(principal.getUser().getId(), dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("???????????? ??????").response(withdrawalRespDto).build(),
                HttpStatus.OK);
    }
}
