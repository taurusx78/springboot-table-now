package com.example.tablenow.web.dto.user;

import com.example.tablenow.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserRespDto {

    private Long id; // 테이블 기본키
    private String username; // 아이디
    private String password; // 비밀번호
    private String name; // 이름
    private String phone; // 휴대폰번호
    private String email; // 이메일

    // 비밀번호 숨기기
    public void hidePassword() {
        this.password = "";
    }

    // User 엔티티를 바탕으로 UserRespDto 객체 생성
    public UserRespDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
    }

    @Getter
    public static class FindIdRespDto {

        private String username; // 아이디
        private String createdDate; // 가입일

        // User 객체를 바탕으로 FindIdRespDto 객체 생성
        public FindIdRespDto(User user) {
            this.username = user.getUsername();
            this.createdDate = user.getCreatedDate().toString();
        }
    }

    @AllArgsConstructor
    @Getter
    public static class WithdrawalRespDto {

        private String name; // 사용자 이름
        private int deletedStoreCount; // 삭제된 매장 수
    }
}
