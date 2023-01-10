package com.example.tablenow.web.dto.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.example.tablenow.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRepDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class LoginReqDto {

        @NotBlank(message = "아이디는 필수항목입니다.")
        @Size(min = 4, max = 20, message = "아이디는 4~20자로 입력해 주세요.")
        private String username; // 아이디

        @NotBlank(message = "비밀번호는 필수항목입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자로 입력해 주세요.")
        private String password; // 비밀번호
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CheckJoinedReqDto {

        @NotBlank(message = "개인고유식별키는 필수항목입니다.")
        private String uniqueKey; // 개인고유식별키
    }

    // Get 요청 시 전송된 파라미터의 유효성 검사를 위해서 Setter도 필요함
    @Data
    public static class CheckUsernameReqDto {

        @NotBlank(message = "아이디는 필수항목입니다.")
        @Size(min = 4, max = 20, message = "아이디는 4~20자로 입력해 주세요.")
        private String username; // 아이디
    }

    // Get 요청 시 전송된 파라미터의 유효성 검사를 위해서 Setter도 필요함
    @Data
    public static class SendAuthNumberReqDto {

        @Size(max = 50, message = "이메일은 50자 이내로 입력해 주세요.") // 이메일 50자 이내
        @Pattern(regexp = "(^[\\w-_]+@[\\w-_\\.]+\\.[A-z]{2,6}$)|()", message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @Pattern(regexp = "(^01(0|1|[6-9])\\d{3,4}\\d{4}$)|()", message = "휴대폰번호 형식이 올바르지 않습니다.")
        private String phone;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CheckAuthNumberReqDto {

        @NotBlank(message = "인증번호는 필수항목입니다.")
        @Pattern(regexp = "\\d{6}", message = "인증번호는 6자리의 숫자로 입력해 주세요.") // 6자리 숫자
        private String authNumber; // 인증번호
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class JoinReqDto {

        @NotBlank(message = "아이디는 필수항목입니다.")
        @Pattern(regexp = "[a-z0-9]{4,20}", message = "아이디는 영어 소문자, 숫자로 구성된 4~20자로 입력해 주세요.")
        private String username; // 아이디

        @NotBlank(message = "비밀번호는 필수항목입니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=])[a-zA-Z\\d~!@#$%^&*()_+=]{8,20}$", message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8~20자로 입력해 주세요.")
        private String password; // 비밀번호

        @NotBlank(message = "이름은 필수항목입니다.")
        @Pattern(regexp = "^[가-힣]{2,20}$", message = "이름은 2~20자의 한글로 입력해 주세요.")
        private String name; // 이름

        @NotBlank(message = "휴대폰번호는 필수항목입니다.")
        @Pattern(regexp = "^01(0|1|[6-9])\\d{3,4}\\d{4}$", message = "휴대폰번호 형식이 올바르지 않습니다.")
        private String phone; // 휴대폰번호

        @NotBlank(message = "개인고유식별키는 필수항목입니다.")
        private String uniqueKey; // 개인고유식별키

        @NotBlank(message = "이메일은 필수항목입니다.")
        @Size(max = 50, message = "이메일은 50자 이내로 입력해 주세요.") // 이메일 50자 이내
        @Pattern(regexp = "^[\\w-_]+@[\\w-_\\.]+\\.[A-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
        private String email; // 이메일

        @NotBlank(message = "인증번호는 필수항목입니다.")
        @Pattern(regexp = "\\d{6}", message = "인증번호는 6자리의 숫자로 입력해 주세요.") // 6자리 숫자
        private String authNumber; // 인증번호

        // 비밀번호 해쉬화
        public void encryptPassword(String password) {
            this.password = password;
        }

        // JoinReqDto를 바탕으로 User 엔티티 생성
        public User toEntity() {
            return User.builder().username(username).password(password).name(name).phone(phone).uniqueKey(uniqueKey)
                    .email(email).build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class FindIdReqDto {

        @NotBlank(message = "인증방식을 선택해 주세요.")
        @Pattern(regexp = "(\\bemail\\b)|(\\bphone\\b)", message = "인증방식은 이메일 또는 휴대폰번호 중 하나입니다.")
        private String method; // 인증방식 (email 또는 phone)

        @NotBlank(message = "이메일 또는 휴대폰번호는 필수항목입니다.")
        @Size(max = 50, message = "이메일은 50자 이내로 입력해 주세요.") // 이메일 50자 이내
        @Pattern(regexp = "(\\w+@\\w+\\.\\w+)|(^01(0|1|[6-9])\\d{3,4}\\d{4}$)", message = "이메일 또는 휴대폰번호 형식이 올바르지 않습니다.")
        private String data; // 이메일 또는 휴대폰번호

        @NotBlank(message = "인증번호는 필수항목입니다.")
        @Pattern(regexp = "\\d{6}", message = "인증번호는 6자리의 숫자로 입력해 주세요.") // 6자리 숫자
        private String authNumber; // 인증번호
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class FindPwReqDto {

        @NotBlank(message = "인증방식을 선택해 주세요.")
        @Pattern(regexp = "(\\bemail\\b)|(\\bphone\\b)", message = "인증방식은 이메일 또는 휴대폰번호 중 하나입니다.")
        private String method; // 인증방식 (email 또는 phone)

        @NotBlank(message = "이메일 또는 휴대폰번호는 필수항목입니다.")
        @Size(max = 50, message = "이메일은 50자 이내로 입력해 주세요.") // 이메일 50자 이내
        @Pattern(regexp = "(\\w+@\\w+\\.\\w+)|(^01(0|1|[6-9])\\d{3,4}\\d{4}$)", message = "이메일 또는 휴대폰번호 형식이 올바르지 않습니다.")
        private String data; // 이메일 또는 휴대폰번호

        @NotBlank(message = "아이디는 필수항목입니다.")
        @Size(min = 4, max = 20, message = "아이디는 4~20자로 입력해 주세요.")
        private String username; // 아이디

        @NotBlank(message = "인증번호는 필수항목입니다.")
        @Pattern(regexp = "\\d{6}", message = "인증번호는 6자리의 숫자로 입력해 주세요.") // 6자리 숫자
        private String authNumber; // 인증번호
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class ResetPwReqDto {

        @NotBlank(message = "아이디는 필수항목입니다.")
        @Size(min = 4, max = 20, message = "아이디는 4~20자로 입력해 주세요.")
        private String username; // 아이디

        @NotBlank(message = "새 비밀번호는 필수항목입니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=])[a-zA-Z\\d~!@#$%^&*()_+=]{8,20}$", message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8~20자로 입력해 주세요.")
        private String newPassword; // 새 비밀번호

        @NotBlank(message = "인증번호는 필수항목입니다.")
        @Pattern(regexp = "\\d{6}", message = "인증번호는 6자리의 숫자로 입력해 주세요.") // 6자리 숫자
        private String authNumber; // 인증번호
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class ChangePwReqDto {

        @NotBlank(message = "현재 비밀번호는 필수항목입니다.")
        @Size(min = 8, max = 20, message = "현재 비밀번호는 8~20자로 입력해 주세요.")
        private String curPassword; // 현재 비밀번호

        @NotBlank(message = "새 비밀번호는 필수항목입니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=])[a-zA-Z\\d~!@#$%^&*()_+=]{8,20}$", message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8~20자로 입력해 주세요.")
        private String newPassword; // 새 비밀번호
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class ChangeEmailReqDto {

        @NotBlank(message = "이메일은 필수항목입니다.")
        @Size(max = 50, message = "이메일은 50자 이내로 입력해 주세요.") // 이메일 50자 이내
        @Pattern(regexp = "^[\\w-_]+@[\\w-_\\.]+\\.[A-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
        private String email; // 이메일

        @NotBlank(message = "인증번호는 필수항목입니다.")
        @Pattern(regexp = "\\d{6}", message = "인증번호는 6자리의 숫자로 입력해 주세요.") // 6자리 숫자
        private String authNumber; // 인증번호
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CheckPwReqDto {

        @NotBlank(message = "비밀번호는 필수항목입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자로 입력해 주세요.")
        private String password; // 비밀번호
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CheckHoursDto {

        private String businessHours;
        private String breakTime;
        private String lastOrder;

        public void resetLastOrder() {
            this.lastOrder = "없음";
        }
    }
}
