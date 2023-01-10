package com.example.tablenow.domain.tables;

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
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Tables {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(nullable = false)
	private int allTableCount; // 전체테이블 수

	@Column(nullable = false)
	private int tableCount; // 잔여테이블 수

    @Column(nullable = false)
	private boolean paused; // 정보제공 임시중지 여부

    // @JsonIgnoreProperties({ "user" }) // Store 엔티티의 user 속성 무시
    @JoinColumn(name = "storeId")
    @OneToOne(fetch = FetchType.LAZY)
    private Store store;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    // 잔여테이블 수 수정
    public void updateTableCountOnly(int tableCount) {
        this.tableCount = tableCount;
    }

    // 잔여테이블 정보 수정
    public void updateTableCount(int tableCount, boolean paused) {
        this.tableCount = tableCount;
        this.paused = paused;
    }

    // 전체테이블 정보 수정
    public void updateAllTableCount(int allTableCount, int tableCount) {
        this.allTableCount = allTableCount;
        this.tableCount = tableCount;
    }
}
