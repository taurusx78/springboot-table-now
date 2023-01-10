package com.example.tablenow.domain.notice;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.tablenow.domain.notice_image.NoticeImage;
import com.example.tablenow.domain.store.Store;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Notice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(nullable = false, length = 50)
	private String title; // 제목
	
	@Column(nullable = false, length = 500)
	private String content; // 내용

    @Column(nullable = false, length = 10)
	private String holidayStartDate; // 임시휴무 시작 날짜
	
	@Column(nullable = false, length = 10)
	private String holidayEndDate; // 임시휴무 종료 날짜

    // 알림 첨부사진
	@JsonIgnoreProperties({ "notice" })
	@OneToMany(mappedBy = "notice", fetch = FetchType.EAGER)
	private List<NoticeImage> noticeImageList;

    // @JsonIgnoreProperties({ "user" }) // Store 엔티티의 user 속성 무시
    @JoinColumn(name = "storeId")
    @OneToOne(fetch = FetchType.LAZY)
    private Store store;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    // 알림 수정
    public void update(String title, String content, String holidayStartDate, String holidayEndDate) {
        this.title = title;
        this.content = content;
        this.holidayStartDate = holidayStartDate;
        this.holidayEndDate = holidayEndDate;
    }
}
