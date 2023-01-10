package com.example.tablenow.web.dto.store;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.example.tablenow.domain.basic_image.BasicImage;
import com.example.tablenow.domain.holidays.Holidays;
import com.example.tablenow.domain.hours.Hours;
import com.example.tablenow.domain.inside_image.InsideImage;
import com.example.tablenow.domain.menu_image.MenuImage;
import com.example.tablenow.domain.store.Store;
import com.example.tablenow.domain.tables.Tables;
import com.example.tablenow.domain.today_hours.TodayHours;
import com.example.tablenow.web.dto.notice.NoticeRespDto;
import com.example.tablenow.web.dto.store.DBStoreRespDto.SelectStateRespDto;
import com.example.tablenow.web.dto.store.DBStoreRespDto.SelectStoreRespDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class StoreRespDto {

	// DTO 리스트 응답 시 해당 DTO로 한 번 더 감싸서 리스트가 아닌 객체로 응답
	@AllArgsConstructor
	@Getter
	public static class StoreListRespDto<T> {

		private List<T> items;
	}

	// 나의 매장 전체조회 시
	@Getter
	public static class MyStoreRespDto {

		private Long id; // 매장 id
		private String name; // 매장명
		private String address; // 도로명주소
		private String detailAddress; // 상세주소
		private String phone; // 전화
		private String basicImageUrl; // 대표사진 1개

		private Date today; // 오늘 날짜
		private int holidayType; // 오늘 휴무 유형 (1 영업일, 2 정기휴무, 3 알림등록 임시휴무, 4 임의변경 임시휴무)
		private String businessHours; // 오늘의 영업시간
		private String breakTime; // 오늘의 휴게시간
		private String lastOrder; // 오늘의 주문마감시간

		private String state; // 영업상태

		// 영업상태를 제외한 생성자
		public MyStoreRespDto(BigInteger id, String name, String address, String detailAddress, String phone,
				String basicImageUrl, Date today, int holidayType, String businessHours, String breakTime,
				String lastOrder) {
			this.id = id.longValue();
			this.name = name;
			this.address = address;
			this.detailAddress = detailAddress;
			this.phone = phone;
			this.basicImageUrl = basicImageUrl;
			this.today = today;
			this.holidayType = holidayType;
			this.businessHours = businessHours;
			this.breakTime = breakTime;
			this.lastOrder = lastOrder;
		}

		// 오늘의 영업정보 변경
		public void updateTodayHours(Date today, int holidayType, String businessHours, String breakTime,
				String lastOrder) {
			this.today = today;
			this.holidayType = holidayType;
			this.businessHours = businessHours;
			this.breakTime = breakTime;
			this.lastOrder = lastOrder;
		}

		// 영업상태 설정
		public void setState(String state) {
			this.state = state;
		}
	}

	// 나의 매장 1개 조회 시
	@Getter
	public static class OneMyStoreRespDto {

		private BigInteger id; // 매장 id
		private String name; // 매장명
		private String basicImageUrl; // 대표사진 1개

		private Date today; // 오늘 날짜
		private int holidayType; // 오늘 휴무 유형 (1 영업일, 2 정기휴무, 3 알림등록 임시휴무, 4 임의변경 임시휴무)
		private String businessHours; // 오늘의 영업시간
		private String breakTime; // 오늘의 휴게시간
		private String lastOrder; // 오늘의 주문마감시간

		private String state; // 영업상태

		private int allTableCount; // 전체테이블 수
		private int tableCount; // 잔여테이블 수
		private boolean paused; // 임시중지 여부
		private String tableModified; // 테이블 수 최종수정일

		// 영업상태를 제외한 생성자
		public OneMyStoreRespDto(BigInteger id, String name, String basicImageUrl, Date today, int holidayType,
				String businessHours, String breakTime, String lastOrder, int allTableCount, int tableCount,
				boolean paused, Timestamp modifiedDate) {
			this.id = id;
			this.name = name;
			this.basicImageUrl = basicImageUrl;
			this.today = today;
			this.holidayType = holidayType;
			this.businessHours = businessHours;
			this.breakTime = breakTime;
			this.lastOrder = lastOrder;
			this.allTableCount = allTableCount;
			this.tableCount = tableCount;
			this.paused = paused;
			this.tableModified = modifiedDate.toString().substring(5, 16).replace("T", " ").replace("-", ".");
			;
		}

		// 오늘의 영업정보 변경
		public void updateTodayHours(Date today, int holidayType, String businessHours, String breakTime,
				String lastOrder) {
			this.today = today;
			this.holidayType = holidayType;
			this.businessHours = businessHours;
			this.breakTime = breakTime;
			this.lastOrder = lastOrder;
		}

		// 영업상태 설정
		public void setState(String state) {
			this.state = state;
		}
	}

	@Getter
	public static class SaveStoreRespDto {

		private Long id; // 매장 id
		private String name; // 매장명
		private String category; // 카테고리
		private String phone; // 전화번호
		private String address; // 도로명주소
		private String detailAddress; // 상세주소
		private String jibunAddress; // 지번주소
		private double latitude; // 위도
		private double longitude; // 경도
		private String description; // 상세설명
		private String website; // 웹사이트

		private int allTableCount; // 전체테이블 수

		private List<String> basicImageUrlList; // 대표사진 리스트
		private List<String> insideImageUrlList; // 매장내부사진 리스트
		private List<String> menuImageUrlList; // 메뉴사진 리스트

		private String holidays; // 정기휴무

		private List<String> businessHoursList; // 영업시간 리스트
		private List<String> breakTimeList; // 휴게시간 리스트
		private List<String> lastOrderList; // 주문마감시간 리스트

		private String today; // 오늘 날짜

		// Store, Hours 엔티티를 바탕으로 SaveSaveRespDto 객체 생성
		public SaveStoreRespDto(Store store, List<String> basicImageUrlList, List<String> insideImageUrlList,
				List<String> menuImageUrlList, String holidays, List<Hours> hoursList, int allTableCount,
				String today) {
			this.id = store.getId();
			this.name = store.getName();
			this.category = store.getCategory();
			this.phone = store.getPhone();
			this.address = store.getAddress();
			this.detailAddress = store.getDetailAddress();
			this.jibunAddress = store.getJibunAddress();
			this.latitude = store.getLatitude();
			this.longitude = store.getLongitude();
			this.description = store.getDescription();
			this.website = store.getWebsite();

			this.basicImageUrlList = basicImageUrlList;
			this.insideImageUrlList = insideImageUrlList;
			this.menuImageUrlList = menuImageUrlList;

			this.holidays = holidays;

			this.businessHoursList = hoursList.stream().map(hours -> hours.getBusinessHours()).toList();
			this.breakTimeList = hoursList.stream().map(hours -> hours.getBreakTime()).toList();
			this.lastOrderList = hoursList.stream().map(hours -> hours.getLastOrder()).toList();

			this.allTableCount = allTableCount;
			this.today = today; // TodayHours 테이블에 저장된 오늘 날짜
		}
	}

	@Getter
	public static class TodayHoursRespDto {

		private int holidayType; // 오늘 휴무 유형 (1 영업일, 2 정기휴무, 3 알림등록 임시휴무, 4 임의변경 임시휴무)
		private String businessHours; // 오늘의 영업시간
		private String breakTime; // 오늘의 휴게시간
		private String lastOrder; // 오늘의 주문마감시간
		private String state; // 영업상태

		// TodayHours 엔티티를 바탕으로 TodayHoursRespDto 객체 생성
		public TodayHoursRespDto(TodayHours todayHours, String state) {
			this.holidayType = todayHours.getHolidayType();
			this.businessHours = todayHours.getBusinessHours();
			this.breakTime = todayHours.getBreakTime();
			this.lastOrder = todayHours.getLastOrder();
			this.state = state;
		}
	}

	@Getter
	public static class TablesRespDto {

		private int allTableCount; // 전체테이블 수
		private int tableCount; // 잔여테이블 수
		private boolean paused; // 임시중지 여부
		private String modifiedDate; // 최종수정일

		// Tables 엔티티를 바탕으로 TablesRespDto 객체 생성
		public TablesRespDto(Tables tables) {
			this.allTableCount = tables.getAllTableCount();
			this.tableCount = tables.getTableCount();
			this.paused = tables.isPaused();
			this.modifiedDate = tables.getModifiedDate().toString().substring(5, 16).replace("T", " ").replace("-",
					".");
		}
	}

	@Builder
	@AllArgsConstructor
	@Getter
	public static class HoursRespDto {

		private Long storeId; // 매장 id

		private String[] openTimeList; // 오픈시간 목록
		private String[] closeTimeList; // 마감시간 목록
		private boolean[] run24HoursList; // 24시영업 여부 목록

		private boolean[] hasBreakTimeList; // 휴게시간 유무 목록
		private String[] startBreakTimeList; // 휴게시간 시작시간 목록
		private String[] endBreakTimeList; // 휴게시간 종료시간 목록

		private boolean[] hasLastOrderList; // 주문마감시간 유무 목록
		private String[] lastOrderList; // 주문마감시간 목록

		private String modifiedDate; // 최종수정일
	}

	@Getter
	public static class UpdateHoursRespDto {

		private List<String> businessHoursList; // 영업시간 리스트
		private List<String> breakTimeList; // 휴게시간 리스트
		private List<String> lastOrderList; // 주문마감시간 리스트

		private int holidayType; // 오늘 휴무 유형 (1 영업일, 2 정기휴무, 3 알림등록 임시휴무, 4 임의변경 임시휴무)
		private String businessHours; // 오늘의 영업시간
		private String breakTime; // 오늘의 휴게시간
		private String lastOrder; // 오늘의 주문마감시간
		private String state; // 영업상태

		// Hours, TodayHours 엔티티를 바탕으로 UpdateHoursRespDto 객체 생성
		public UpdateHoursRespDto(List<Hours> hoursList, TodayHours todayHours, String state) {
			this.businessHoursList = hoursList.stream().map(hours -> hours.getBusinessHours()).toList();
			this.breakTimeList = hoursList.stream().map(hours -> hours.getBreakTime()).toList();
			this.lastOrderList = hoursList.stream().map(hours -> hours.getLastOrder()).toList();

			this.holidayType = todayHours.getHolidayType();
			this.businessHours = todayHours.getBusinessHours();
			this.breakTime = todayHours.getBreakTime();
			this.lastOrder = todayHours.getLastOrder();
			this.state = state;
		}
	}

	@Getter
	public static class HolidaysRespDto {

		private Long storeId; // 매장 id
		private String holidays; // 정기휴무 목록
		private String modifiedDate; // 최종수정일

		// Holidays 엔티티를 바탕으로 HolidaysRespDto 객체 생성
		public HolidaysRespDto(Long storeId, Holidays holidays) {
			this.storeId = storeId;
			this.holidays = holidays.getHolidays();
			this.modifiedDate = holidays.getModifiedDate().toString();
		}
	}

	@Getter
	public static class UpdateHolidaysRespDto {

		private String holidays; // 정기휴무

		private int holidayType; // 오늘 휴무 유형 (1 영업일, 2 정기휴무, 3 알림등록 임시휴무, 4 임의변경 임시휴무)
		private String businessHours; // 오늘의 영업시간
		private String breakTime; // 오늘의 휴게시간
		private String lastOrder; // 오늘의 주문마감시간
		private String state; // 영업상태

		// TodayHours 엔티티를 바탕으로 UpdateHolidaysRespDto 객체 생성
		public UpdateHolidaysRespDto(String holidays, TodayHours todayHours, String state) {
			this.holidays = holidays;
			this.holidayType = todayHours.getHolidayType();
			this.businessHours = todayHours.getBusinessHours();
			this.breakTime = todayHours.getBreakTime();
			this.lastOrder = todayHours.getLastOrder();
			this.state = state;
		}
	}

	@Getter
	public static class MenuRespDto {

		private Long storeId; // 매장 id
		private List<String> imageUrlList; // 메뉴사진 리스트
		private String modifiedDate; // 최종수정일

		// MenuImage 엔티티를 바탕으로 MenuRespDto 객체 생성
		public MenuRespDto(Long storeId, List<MenuImage> menuImageList, Date modifiedDate) {
			this.storeId = storeId;
			imageUrlList = menuImageList.stream().map(menuImage -> menuImage.getImageUrl()).toList();
			this.modifiedDate = modifiedDate.toString();
		}
	}

	@Getter
	public static class UpdateMenuRespDto {

		private List<String> menuImageUrlList; // 메뉴사진 리스트
		private int deletedCount; // 삭제된 메뉴사진 개수

		// MenuImage 엔티티를 바탕으로 UpdateMenuRespDto 객체 생성
		public UpdateMenuRespDto(List<MenuImage> menuImageList, int deletedCount) {
			this.menuImageUrlList = menuImageList.stream().map(image -> image.getImageUrl()).toList();
			this.deletedCount = deletedCount;
		}
	}

	@Getter
	public static class InsideRespDto {

		private Long storeId; // 매장 id
		private int allTableCount; // 전체테이블 수
		private List<String> imageUrlList; // 매장내부사진 리스트
		private String insideModified; // 매장내부사진 최종수정일

		// InsideImage 엔티티를 받아 InsideInfoResp 객체 생성
		public InsideRespDto(Long storeId, int allTableCount, List<InsideImage> insideImageList, Date modifiedDate) {
			this.storeId = storeId;
			this.allTableCount = allTableCount;
			imageUrlList = insideImageList.stream().map(insideImage -> insideImage.getImageUrl()).toList();
			insideModified = modifiedDate.toString();
		}
	}

	@Getter
	public static class UpdateInsideRespDto {

		private int allTableCount; // 전체테이블 수
		private int tableCount; // 잔여테이블 수

		private List<String> insideImageUrlList; // 매장내부사진 리스트
		private int deletedCount; // 삭제된 메뉴사진 개수

		// Tables, InsideImage 엔티티를 바탕으로 UpdateInsideRespDto 객체 생성
		public UpdateInsideRespDto(Tables tables, List<InsideImage> insideImageList, int deletedCount) {
			this.allTableCount = tables.getAllTableCount();
			this.tableCount = tables.getTableCount();

			this.insideImageUrlList = insideImageList.stream().map(image -> image.getImageUrl()).toList();
			this.deletedCount = deletedCount;
		}
	}

	@Getter
	public static class BasicRespDto {

		private BigInteger storeId; // 매장 id
		private String name; // 매장명
		private String category; // 카테고리
		private String phone; // 전화번호
		private String address; // 도로명주소
		private String detailAddress; // 상세주소
		private String jibunAddress; // 지번주소
		private Double latitude; // 위도
		private Double longitude; // 경도
		private String description; // 상세설명
		private String website; // 웹사이트
		private String storeModified; // 매장 최종수정일

		private List<String> imageUrlList; // 대표사진 리스트

		private String modifiedDate; // 기본정보 최종수정일 (Store와 BasicImage 중 최근 수정한 날짜)

		// 대표사진 리스트, 기본정보 최종수정일을 제외한 생성자
		public BasicRespDto(BigInteger storeId, String name, String category, String phone, String address,
				String detailAddress, String jibunAddress, Double latitude, Double longitude, String description,
				String website, Timestamp storeModified) {
			this.storeId = storeId;
			this.name = name;
			this.category = category;
			this.phone = phone;
			this.address = address;
			this.detailAddress = detailAddress;
			this.jibunAddress = jibunAddress;
			this.latitude = latitude;
			this.longitude = longitude;
			this.description = description;
			this.website = website;
			this.storeModified = storeModified.toString().substring(0, 10);
		}

		public void setImageInfo(List<BasicImage> basicImageList, Date basicImageModified) {
			this.imageUrlList = basicImageList.stream().map(basicImage -> basicImage.getImageUrl()).toList();
			modifiedDate = basicImageModified.toString();
			if (storeModified.compareTo(modifiedDate) > 0) {
				modifiedDate = storeModified;
			}
		}
	}

	@Getter
	public static class UpdateBasicRespDto {

		private String name; // 매장명
		private String category; // 카테고리
		private String phone; // 전화번호
		private String address; // 도로명주소
		private String detailAddress; // 상세주소
		private String jibunAddress; // 지번주소
		private double latitude; // 위도
		private double longitude; // 경도
		private String description; // 상세설명
		private String website; // 웹사이트

		private String basicImageUrl; // 대표사진 1개

		private List<String> basicImageUrlList; // 대표사진 리스트
		private int deletedCount; // 삭제된 대표사진 개수

		// Store, BasicImage 엔티티를 바탕으로 UpdateBasicRespDto 객체 생성
		public UpdateBasicRespDto(Store store, String basicImageUrl, List<BasicImage> basicImageList,
				int deletedCount) {
			this.name = store.getName();
			this.category = store.getCategory();
			this.phone = store.getPhone();
			this.address = store.getAddress();
			this.detailAddress = store.getDetailAddress();
			this.jibunAddress = store.getJibunAddress();
			this.latitude = store.getLatitude();
			this.longitude = store.getLongitude();
			this.description = store.getDescription();
			this.website = store.getWebsite();
			this.basicImageUrl = basicImageUrl;

			this.basicImageUrlList = basicImageList.stream().map(image -> image.getImageUrl()).toList();
			this.deletedCount = deletedCount;
		}
	}

	@AllArgsConstructor
	@Getter
	public static class DeleteStoreRespDto {

		private String name; // 매장명
		private int deletedBasicImageCount; // 삭제된 대표사진 개수
		private int deletedInsideImageCount; // 삭제된 매장내부사진 개수
		private int deletedMenuImageCount; // 삭제된 메뉴사진 개수
		private int deletedNoticeCount; // 삭제된 알림 개수
	}

	@AllArgsConstructor
	@Getter
	public static class NameRespDto {

		private BigInteger id; // 매장 id
		private String name; // 매장명
	}

	@Data
	public static class SpecificStoreRespDto {

		private Long id; // 매장 id
		private String name; // 매장명
		private String jibunAddress; // 지번주소
		private String basicImageUrl; // 대표사진 1개
		private String state; // 영업상태
		private int tableCount; // 잔여테이블 수
		private String updated; // 업데이트 시간
		private float distance; // 현재 위치와의 거리

		// SelectStoreRespDto 객체를 받아 SpecificStoreRespDto 객체 생성
		public SpecificStoreRespDto(SelectStoreRespDto dto, String state) {
			this.id = dto.getId();
			this.name = dto.getName();
			this.jibunAddress = dto.getJibunAddress();
			this.basicImageUrl = dto.getBasicImageUrl();
			this.state = state;
			this.tableCount = !dto.isPaused() ? dto.getTableCount() : -1;
			this.updated = dto.getTodayHoursModified().after(dto.getTablesModified())
					? dto.getTodayHoursModified().toString()
					: dto.getTablesModified().toString();
		}
	}

	@Getter
	public static class DetailsRespDto {

		private Long id; // 매장 id
		private String name; // 매장명
		private String category; // 카테고리
		private String phone; // 전화
		private String address; // 도로명주소
		private String detailAddress; // 상세주소
		private String jibunAddress; // 지번주소
		private double latitude; // 위도
		private double longitude; // 경도
		private String description; // 소개
		private String website; // 웹사이트

		private List<String> basicImageUrlList; // 대표사진 리스트
		private List<String> insideImageUrlList; // 매장내부사진 리스트
		private String insideModified; // 매장내부사진 최종수정일
		private List<String> menuImageUrlList; // 메뉴사진 리스트
		private String menuModified; // 메뉴사진 최종수정일

		private int allTableCount; // 전체테이블 수
		private int tableCount; // 잔여테이블 수

		private String businessHours; // 오늘의 영업시간
		private String breakTime; // 오늘의 휴게시간
		private String lastOrder; // 오늘의 주문마감시간

		private String state; // 영업상태
		private String updated; // 업데이트 시간

		private List<NoticeRespDto> noticeList; // 알림 리스트

		private List<BlogRespDto> blogList; // 블로그 리뷰 리스트

		// Store, NoticeRespDto, BlogRespDto 객체를 받아 DetailsRespDto 객체 생성
		public DetailsRespDto(Store store, String state, List<NoticeRespDto> noticeList, List<BlogRespDto> blogList) {
			id = store.getId();
			name = store.getName();
			category = store.getCategory();
			phone = store.getPhone();
			address = store.getAddress();
			detailAddress = store.getDetailAddress();
			jibunAddress = store.getJibunAddress();
			latitude = store.getLatitude();
			longitude = store.getLongitude();
			description = store.getDescription();
			website = store.getWebsite();

			List<BasicImage> basicImageList = store.getBasicImageList();
			basicImageUrlList = basicImageList.stream().map(basicImage -> basicImage.getImageUrl()).toList();

			List<InsideImage> insideImageList = store.getInsideImageList();
			insideImageUrlList = insideImageList.stream().map(insideImage -> insideImage.getImageUrl()).toList();
			insideModified = insideImageList.get(insideImageList.size() - 1).getCreatedDate().toString().substring(2)
					.replace("-", ".");

			List<MenuImage> menuImageList = store.getMenuImageList();
			menuImageUrlList = menuImageList.stream().map(menuImage -> menuImage.getImageUrl()).toList();
			menuModified = menuImageList.get(menuImageList.size() - 1).getCreatedDate().toString().substring(2).replace(
					"-",
					".");

			Tables tables = store.getTables();
			allTableCount = tables.getAllTableCount();
			tableCount = !tables.isPaused() ? tables.getTableCount() : -1; // 테이블수 제공 임시중지된 경우 -1

			TodayHours todayHours = store.getTodayHours();
			businessHours = todayHours.getBusinessHours();
			breakTime = todayHours.getBreakTime();
			lastOrder = todayHours.getLastOrder();

			LocalDateTime temp = tables.getModifiedDate().isAfter(todayHours.getModifiedDate())
					? tables.getModifiedDate()
					: todayHours.getModifiedDate();
			updated = temp.toString();

			this.state = state;
			this.noticeList = noticeList;
			this.blogList = blogList;
		}
	}

	@Builder
	@AllArgsConstructor
	@Getter
	public static class BlogRespDto {

		private String title; // 제목
		private String description; // 내용
		private String bloggername; // 작성자
		private String postdate; // 게시일
		private String link; // 블로그 링크
	}

	@AllArgsConstructor
	@Getter
	public static class StateRespDto {

		private String state; // 영업상태
		private int tableCount; // 잔여테이블 수
		private String updated; // 업데이트 시간 (TodayHours 엔티티과 Tables 엔티티의 ModifiedDate 중 최신 날짜)
		private String businessHours; // 오늘의 영업시간
		private String breakTime; // 오늘의 휴게시간
		private String lastOrder; // 오늘의 주문마감시간

		// SelectStateRespDto 객체를 받아 StateRespDto 객체 생성
		public StateRespDto(SelectStateRespDto dto, String state) {
			this.state = state;
			this.tableCount = !dto.getIsPaused() ? dto.getTableCount() : -1;
			Timestamp temp = dto.getTodayModified().after(dto.getTablesModified())
					? dto.getTodayModified()
					: dto.getTablesModified();
			updated = temp.toString();
			this.businessHours = dto.getBusinessHours();
			this.breakTime = dto.getBreakTime();
			this.lastOrder = dto.getLastOrder();
		}
	}

	@AllArgsConstructor
	@Getter
	public static class WeeklyHoursRespDto {

		private String[][] weeklyHours;
	}
}
