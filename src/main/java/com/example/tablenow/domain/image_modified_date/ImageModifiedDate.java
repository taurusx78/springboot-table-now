package com.example.tablenow.domain.image_modified_date;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.tablenow.domain.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "image_modified_date")
@Entity
public class ImageModifiedDate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(nullable = false)
    private LocalDate basicModified; // 대표사진 최종수정일

    @Column(nullable = false)
    private LocalDate insideModified; // 매장내부사진 최종수정일

    @Column(nullable = false)
    private LocalDate menuModified; // 메뉴사진 최종수정일

    @JoinColumn(name = "storeId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;
}
