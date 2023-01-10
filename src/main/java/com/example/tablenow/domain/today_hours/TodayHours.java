package com.example.tablenow.domain.today_hours;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.tablenow.domain.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "today_hours")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class TodayHours {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(nullable = false)
	private int holidayType; // 휴무 여부 (1 영업일, 2 정기휴무, 3 알림등록 임시휴무, 4 임의변경 임시휴무)
	
	@Column(nullable = false, length = 13)
	private String businessHours; // 영업시간
	
	@Column(nullable = false, length = 13)
	private String breakTime; // 휴게시간
	
	@Column(nullable = false, length = 5)
	private String lastOrder; // 주문 마감

    @Column(nullable = false)
	private LocalDate today; // 오늘 날짜 (날짜 변경 여부 확인용)

    // @JsonIgnoreProperties({ "user" }) // Store 엔티티의 user 속성 무시
    @JoinColumn(name = "storeId")
    @OneToOne(fetch = FetchType.LAZY)
    private Store store;

    @CreatedDate
    private LocalDateTime createdDate;

    // @LastModifiedDate
    private LocalDateTime modifiedDate;

    // 오늘의 영업시간 수정 (날짜 변경)
    public void update(int holidayType, String businessHours, String breakTime, String lastOrder) {
        this.holidayType = holidayType;
        this.businessHours = businessHours;
        this.breakTime = breakTime;
        this.lastOrder = lastOrder;
    }

    // 오늘의 영업시간 수정 (직접 변경)
    public void updateInPerson(int holidayType, String businessHours, String breakTime, String lastOrder) {
        this.holidayType = holidayType;
        this.businessHours = businessHours;
        this.breakTime = breakTime;
        this.lastOrder = lastOrder;
        this.modifiedDate = LocalDateTime.now(); // 최종수정일 직접 변경
    }

    // 날짜 변경 반영
    public void changeToday(LocalDate date) {
        this.today = date;
    }
}
