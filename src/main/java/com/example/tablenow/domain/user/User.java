package com.example.tablenow.domain.user;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "users") // user는 예약어이기 때문에 users로 변경
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    // length : 한영 구분 없이 글자수 나타냄
    @Column(nullable = false, unique = true, length = 20) 
    private String username; // 아이디

    // 해쉬 결과를 저장하므로 길이 넉넉하게
    @Column(nullable = false, length = 100)
    private String password; // 비밀번호

    @Column(nullable = false, length = 20)
    private String name; // 이름

    @Column(nullable = false, unique = true, length = 11) // '-' 제외
    private String phone; // 휴대폰번호

    @Column(nullable = false, unique = true)
    private String uniqueKey; // 개인고유식별키

    @Column(nullable = false, length = 50)
    private String email; // 이메일

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    // 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }

    // 이메일 변경
    public void changeEmail(String email) {
        this.email = email;
    }

    // 휴대폰번호 변경
    public void changePhone(String phone) {
        this.phone = phone;
    }
}
