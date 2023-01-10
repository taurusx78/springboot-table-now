package com.example.tablenow.domain.inside_image;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "inside_image")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class InsideImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(nullable = false, unique = true)
    private String imageUrl; // 서버에 저장된 이미지 경로

    @JoinColumn(name = "storeId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @CreatedDate
    private LocalDateTime createdDate;
}
