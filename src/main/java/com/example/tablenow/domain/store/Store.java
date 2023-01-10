package com.example.tablenow.domain.store;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.tablenow.domain.basic_image.BasicImage;
import com.example.tablenow.domain.inside_image.InsideImage;
import com.example.tablenow.domain.menu_image.MenuImage;
import com.example.tablenow.domain.notice.Notice;
import com.example.tablenow.domain.tables.Tables;
import com.example.tablenow.domain.today_hours.TodayHours;
import com.example.tablenow.domain.user.User;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateBasicReqDto;
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
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(nullable = false, length = 50)
    private String name; // 매장명

    @Column(nullable = false, unique = true, length = 10)
    private String businessRegNum; // 사업자등록번호

    @Column(nullable = false, length = 10)
    private String category; // 카테고리(업종)

    @Column(nullable = false, length = 13) // '-' 포함
    private String phone; // 전화번호

    @Column(nullable = false, length = 50)
    private String address; // 도로명주소

    @Column(nullable = false, length = 50)
    private String detailAddress; // 상세주소

    @Column(nullable = false, length = 20)
    private String jibunAddress; // 지번주소

    @Column(nullable = false)
    private double latitude; // 위도

    @Column(nullable = false)
    private double longitude; // 경도

    @Column(nullable = false, length = 500)
    private String description; // 상세설명

    @Column(nullable = false, length = 100)
    private String website; // 웹사이트

    // 대표사진
    // @JsonIgnoreProperties : Store 엔티티를 JSON으로 변환 시, BusinessHours 엔티티의 store 변수를
    // 무시하도록 설정 (무한 참조 방지)
    // @OneToMany: Store 엔티티는 연관관계의 주인이 아님 (컬럼 생성 X), 기본 FetchType은 LAZY
    // FetchType = LAZY: getBasicImageList()가 호출될 때 BasicImage 엔티티 SELECT 함
    @JsonIgnoreProperties({ "store" })
    @OneToMany(mappedBy = "store")
    private List<BasicImage> basicImageList;

    // 매장내부사진
    @JsonIgnoreProperties({ "store" })
    @OneToMany(mappedBy = "store")
    private List<InsideImage> insideImageList;

    // 메뉴사진
    @JsonIgnoreProperties({ "store" })
    @OneToMany(mappedBy = "store")
    private List<MenuImage> menuImageList;

    // 오늘의 영업시간
    // (주의) @OneToOne은 연관관계의 주인이 아닌 곳에서 무조건 EAGER 전략 가짐 (변경 불가)
    @JsonIgnoreProperties({ "store" })
    @OneToOne(mappedBy = "store")
    private TodayHours todayHours;

    // 테이블수
    @JsonIgnoreProperties({ "store" })
    @OneToOne(mappedBy = "store")
    private Tables tables;

    // 매장알림 + 첨부사진
    @JsonIgnoreProperties({ "store" })
    @OneToMany(mappedBy = "store", fetch = FetchType.EAGER)
    private List<Notice> noticeList;

    // @JoinColumn(name = "컬럼명"): 해당 이름을 가진 컬럼 생성
    // @ManyToOne의 기본 FetchType은 EAGER
    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    // 매장 정보 수정
    public void update(UpdateBasicReqDto dto) {
        this.phone = dto.getPhone();
        this.address = dto.getAddress();
        this.detailAddress = dto.getDetailAddress();
        this.jibunAddress = dto.getJibunAddress();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
        this.description = dto.getDescription();
        this.website = dto.getWebsite();
    }

    // 테스트용 Setter
    public void setForTest(List<BasicImage> basicImageList, List<InsideImage> insideImageList,
            List<MenuImage> menuImageList, TodayHours todayHours, Tables tables, List<Notice> noticeList) {
        this.basicImageList = basicImageList;
        this.insideImageList = insideImageList;
        this.menuImageList = menuImageList;
        this.todayHours = todayHours;
        this.tables = tables;
        this.noticeList = noticeList;
    }
}
