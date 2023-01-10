package com.example.tablenow.domain.notice_image;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.tablenow.domain.notice.Notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "notice_image")
@Entity
public class NoticeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(nullable = false, unique = true)
    private String imageUrl; // 서버에 저장된 이미지의 경로

    @JoinColumn(name = "noticeId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Notice notice;
}
