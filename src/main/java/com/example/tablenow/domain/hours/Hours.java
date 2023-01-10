package com.example.tablenow.domain.hours;

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
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "hours_uk", columnNames = { "dayType", "storeId" })
}) // (dayType, storeId) 컬럼을 한 쌍으로 유니크한 복합키 생성
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Hours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(nullable = false)
    private int dayType; // 요일 (day는 예약어이기 때문에 사용불가)

    @Column(nullable = false, length = 13)
    private String businessHours; // 영업시간

    @Column(nullable = false, length = 13)
    private String breakTime; // 휴게시간

    @Column(nullable = false, length = 13)
    private String lastOrder; // 주문마감시간

    @JoinColumn(name = "storeId")
    @OneToOne(fetch = FetchType.LAZY)
    private Store store;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    // 영업시간, 휴게시간, 주문마감시간 수정
    public void update(String businessHours, String breakTime, String lastOrder) {
        this.businessHours = businessHours;
        this.breakTime = breakTime;
        this.lastOrder = lastOrder;
    }
}
