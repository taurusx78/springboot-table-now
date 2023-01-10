package com.example.tablenow.web.dto.store;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;

// DB에서 SELECT한 결과를 담을 DTO

public class DBStoreRespDto {

    // 매장 전체조회 요청 시 DB에서 SELECT한 결과를 담을 DTO (고객용)
    // 데이터 가공을 거쳐 필요한 데이터만 SpecificStoreRespDto에 담아 응답할 예정
    @AllArgsConstructor
    @Getter
    public static class SelectStoreRespDto {

        private Long id; // 매장 id
        private String name; // 매장명
        private String jibunAddress; // 지번주소
        private String basicImageUrl; // 대표사진 1개

        private Date today; // 오늘 날짜
        private int holidayType; // 오늘 휴무 유형 (1 영업일, 2 정기휴무, 3 알림등록 임시휴무, 4 임의변경 임시휴무)
        private String businessHours; // 오늘의 영업시간
        private String breakTime; // 오늘의 휴게시간
        private String lastOrder; // 오늘의 주문마감시간
        private Timestamp todayHoursModified; // TodayHours 테이블 수정일

        private int tableCount; // 잔여테이블 수
        private boolean paused; // 테이블수 제공 임시중지 여부
        private Timestamp tablesModified; // Tables 테이블 수정일

        private double distance; // 현재 위치와의 거리

        // 현재위치를 제외한 생성자 생성
        public SelectStoreRespDto(BigInteger id, String name, String jibunAddress,
                String basicImageUrl, Date today, int holidayType, String businessHours, String breakTime,
                String lastOrder, Timestamp todayHoursModified, int tableCount, boolean paused,
                Timestamp tablesModified) {
            this.id = id.longValue();
            this.name = name;
            this.jibunAddress = jibunAddress;
            this.basicImageUrl = basicImageUrl;
            this.today = today;
            this.holidayType = holidayType;
            this.businessHours = businessHours;
            this.breakTime = breakTime;
            this.lastOrder = lastOrder;
            this.todayHoursModified = todayHoursModified;
            this.tableCount = tableCount;
            this.paused = paused;
            this.tablesModified = tablesModified;
        }

        // 현재 위치와의 거리 설정
        public void setDistance(double distance) {
            this.distance = distance;
        }
    }

    // 영업상태 업데이트 요청 시 DB에서 SELECT한 결과를 담을 DTO (고객용)
    // 데이터 가공을 거쳐 필요한 데이터만 StateResp에 담아 사용자에게 응답할 예정
    @AllArgsConstructor
    @Getter
    public static class SelectStateRespDto {

        private int holidayType; // 휴무 여부 (1 영업일, 2 정기휴무, 3 알림등록 임시휴무, 4 임의변경 임시휴무)
        private String businessHours; // 영업시간
        private String breakTime; // 휴게시간
        private String lastOrder; // 주문마감시간
        private Timestamp todayModified; // Today 수정일

        private int tableCount; // 잔여테이블 수
        private Boolean isPaused; // 테이블정보 임시중지 여부
        private Timestamp tablesModified; // Tables 수정일
    }

    // 매장정보 수정제안 요청 시 DB에서 SELECT한 매장의 User 정보를 담을 DTO (고객용)
    @AllArgsConstructor
    @Getter
    public static class SelectUserRespDto {

        private String storeName; // 매장명
        private String managerName; // 사장님 이름
        private String email; // 사장님 이메일
    }
}
