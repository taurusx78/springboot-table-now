package com.example.tablenow.web.dto.store;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.example.tablenow.domain.basic_image.BasicImage;
import com.example.tablenow.domain.holidays.Holidays;
import com.example.tablenow.domain.hours.Hours;
import com.example.tablenow.domain.image_modified_date.ImageModifiedDate;
import com.example.tablenow.domain.inside_image.InsideImage;
import com.example.tablenow.domain.menu_image.MenuImage;
import com.example.tablenow.domain.store.Store;
import com.example.tablenow.domain.tables.Tables;
import com.example.tablenow.domain.today_hours.TodayHours;
import com.example.tablenow.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StoreReqDto {

	@AllArgsConstructor
	@Getter
	public static class CheckExistReqDto {

		@NotBlank(message = "사업자등록번호는 필수항목입니다.")
		@Pattern(regexp = "\\d{10}", message = "사업자등록번호는 10자리의 숫자로 입력해 주세요.")
		private String businessRegNum; // 사업자등록번호
	}

	@AllArgsConstructor
	@Getter
	public static class SaveStoreReqDto {

		@NotBlank(message = "매장명은 필수항목입니다.")
		@Size(max = 50, message = "매장명은 50자 이내로 입력해 주세요.")
		private String name; // 매장명

		@NotBlank(message = "사업자등록번호는 필수항목입니다.")
		@Pattern(regexp = "\\d{10}", message = "사업자등록번호는 10자리의 숫자로 입력해 주세요.")
		private String businessRegNum; // 사업자등록번호

		@NotBlank(message = "카테고리(업종)는 필수항목입니다.")
		@Pattern(regexp = "^([가-힣]|/){1,10}$", message = "카테고리 형식이 올바르지 않습니다.")
		private String category; // 카테고리(업종)

		@NotBlank(message = "매장 전화번호는 필수항목입니다.")
		@Pattern(regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$", message = "매장 전화번호 형식이 올바르지 않습니다.")
		private String phone; // 전화번호

		@NotBlank(message = "매장 도로명주소는 필수항목입니다.")
		@Pattern(regexp = "^([가-힣]|\\w|[,-]|\\s){2,50}$", message = "매장 도로명주소 형식이 올바르지 않습니다.)")
		private String address; // 도로명주소

		@NotBlank(message = "매장 상세주소는 필수항목입니다.")
		@Pattern(regexp = "^([가-힣]|\\w|[,-]|\\s){2,50}$", message = "매장 상세주소 형식이 올바르지 않습니다.")
		private String detailAddress; // 상세주소

		@NotBlank(message = "매장 지번주소는 필수항목입니다.")
		@Pattern(regexp = "^([가-힣]|\\s){2,20}$", message = "매장 지번주소는 2~20자의 한글로 입력해 주세요.")
		private String jibunAddress; // 지번주소

		@NotNull(message = "매장 위치의 위도는 필수항목입니다.")
		private double latitude; // 위도

		@NotNull(message = "매장 위치의 경도는 필수항목입니다.")
		private double longitude; // 경도

		@NotBlank(message = "매장 소개는 필수항목입니다.")
		@Size(max = 500, message = "매장 소개는 500자 이내로 입력해 주세요.")
		private String description; // 매장 소개

		@NotNull(message = "웹사이트는 Null일 수 없습니다.") // 공백은 가능
		@Pattern(regexp = "(https?:\\/\\/)?(www\\.)?[\\w@:%.-_\\+~#=]{2,256}\\.[a-z]{2,6}\\b([\\w@:%-_\\+.~#?&//=]*)|()", message = "웹사이트 형식이 올바르지 않습니다.")
		private String website; // 웹사이트

		@NotNull(message = "전체테이블 수는 필수항목입니다.")
		@PositiveOrZero(message = "전체테이블 수는 0 이상의 숫자로 입력해 주세요.")
		@Max(value = 500, message = "전체테이블 최대 개수는 500개입니다.")
		private int allTableCount; // 전체테이블 수

		@NotEmpty(message = "대표사진을 최소 1장 올려주세요.") // 최소 1장
		@Size(max = 3, message = "대표사진은 3장 이하로 올려주세요.") // 최대 3장
		private List<MultipartFile> basicImageFileList; // 대표사진 파일 리스트

		@NotEmpty(message = "매장내부사진을 최소 1장 올려주세요.") // 최소 1장
		@Size(max = 10, message = "매장내부사진은 10장 이하로 올려주세요.") // 최대 10장
		private List<MultipartFile> insideImageFileList; // 매장내부사진 파일 리스트

		@NotEmpty(message = "메뉴사진을 최소 1장 올려주세요.") // 최소 1장
		@Size(max = 20, message = "메뉴사진은 20장 이하로 올려주세요.") // 최대 20장
		private List<MultipartFile> menuImageFileList; // 메뉴사진 파일 리스트

		@NotNull(message = "정기휴무는 Null일 수 없습니다.") // 공백은 가능
		@Pattern(regexp = "([0-5][0-6]\\s){0,42}", message = "정기휴무 입력 형식이 올바르지 않습니다.")
		private String holidays; // 정기휴무 (형식: "10 11 ", ** 맨끝에 공백 필수!)

		@NotEmpty(message = "영업시간 리스트는 필수항목입니다.")
		@Size(min = 7, max = 7, message = "영업시간 리스트의 크기는 7과 같아야 합니다.")
		private List<String> businessHoursList; // 영업시간 리스트

		@NotEmpty(message = "휴게시간 리스트는 필수항목입니다.")
		@Size(min = 7, max = 7, message = "영업시간 리스트의 크기는 7과 같아야 합니다.")
		private List<String> breakTimeList; // 휴게시간 리스트

		@NotEmpty(message = "주문마감시간 리스트는 필수항목입니다.")
		@Size(min = 7, max = 7, message = "영업시간 리스트의 크기는 7과 같아야 합니다.")
		private List<String> lastOrderList; // 주문마감시간 리스트

		// 주문마감시간 없음으로 재설정
		public void resetLastOrder(int index) {
			lastOrderList.set(index, "없음");
		}

		// Store 객체 생성
		public Store toStoreEntity(User user) {
			return Store.builder().name(name).businessRegNum(businessRegNum).category(category).phone(phone)
					.address(address).detailAddress(detailAddress).jibunAddress(jibunAddress)
					.latitude(latitude).longitude(longitude).description(description).website(website).user(user)
					.build();
		}

		// BasicImage 객체 생성
		public BasicImage toBasicImageEntity(String imageUrl, Store store) {
			return BasicImage.builder().imageUrl(imageUrl).store(store).build();
		}

		// InsideImage 객체 생성
		public InsideImage toInsideImageEntity(String imageUrl, Store store) {
			return InsideImage.builder().imageUrl(imageUrl).store(store).build();
		}

		// MenuImage 객체 생성
		public MenuImage toMenuImageEntity(String imageUrl, Store store) {
			return MenuImage.builder().imageUrl(imageUrl).store(store).build();
		}

		// ImageModifiedDate 객체 생성
		public ImageModifiedDate toImageModifiedDate(Store store) {
			LocalDate now = LocalDate.now();
			return ImageModifiedDate.builder().basicModified(now).insideModified(now).menuModified(now).store(store)
					.build();
		}

		// Holidays 객체 생성
		public Holidays toHolidaysEntity(Store store) {
			// 입력받은 holidays 오름차순 정렬
			String[] splitedHolidays = holidays.split(" ");
			Arrays.sort(splitedHolidays);
			String sortedHolidays = "";
			for (String holiday : splitedHolidays) {
				sortedHolidays += holiday + " ";
			}
			holidays = sortedHolidays.trim();

			return Holidays.builder().holidays(holidays.trim()).store(store).build();
		}

		// Hours 객체 리스트 생성
		public List<Hours> toHoursEntityList(Store store) {
			List<Hours> hoursList = new ArrayList<>();
			for (int i = 0; i < 7; i++) {
				hoursList.add(Hours.builder().dayType(i).businessHours(businessHoursList.get(i))
						.breakTime(breakTimeList.get(i)).lastOrder(lastOrderList.get(i)).store(store).build());
			}
			return hoursList;
		}

		// Tables 객체 생성
		public Tables toTablesEntity(Store store) {
			return Tables.builder().allTableCount(allTableCount).tableCount(allTableCount).paused(false).store(store)
					.build();
		}

		// TodayHours 객체 생성
		public TodayHours toTodayHoursEntity(Store store) {
			return TodayHours.builder().today(LocalDate.now()).businessHours("").breakTime("").lastOrder("")
					.modifiedDate(LocalDateTime.now()).store(store).build();
		}
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class UpdateTodayHoursReqDto {

		@NotNull(message = "오늘 휴무 여부는 필수항목입니다.")
		private boolean holiday; // 오늘 휴무 여부

		@NotBlank(message = "오늘의 영업시간은 필수항목입니다.")
		@Pattern(regexp = "(^[0-2][0-9]:[0-5][0-9] - [0-2][0-9]:[0-5][0-9])|([없][음])$", message = "영업시간 형식이 올바르지 않습니다.")
		private String businessHours; // 오늘의 영업시간

		@NotBlank(message = "오늘의 휴게시간은 필수항목입니다.")
		@Pattern(regexp = "^([0-2][0-9]:[0-5][0-9] - [0-2][0-9]:[0-5][0-9])|([없][음])$", message = "휴게시간 형식이 올바르지 않습니다.")
		private String breakTime; // 오늘의 휴게시간

		@NotBlank(message = "오늘의 주문마감시간은 필수항목입니다.")
		@Pattern(regexp = "^([0-2][0-9]:[0-5][0-9])|([없][음])$", message = "주문마감시간 형식이 올바르지 않습니다.")
		private String lastOrder; // 오늘의 주문마감시간

		// 주문마감시간 없음으로 변경
		public void resetLastOrder() {
			this.lastOrder = "없음";
		}
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class UpdateTablesReqDto {

		@NotNull(message = "테이블 수 조작 유형을 선택해 주세요.")
		private int type; // 감소 (0), 증가 (1), 초기화 (2), 임시중지 (3)

		@NotNull(message = "테이블 정보 제공 임시중지 여부는 필수항목입니다.")
		private boolean paused; // 임시중지 여부
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class UpdateHoursReqDto {

		@NotEmpty(message = "영업시간 리스트는 필수항목입니다.")
		@Size(min = 7, max = 7, message = "영업시간 리스트의 크기는 7과 같아야 합니다.")
		private List<String> businessHoursList; // 영업시간 리스트

		@NotEmpty(message = "휴게시간 리스트는 필수항목입니다.")
		@Size(min = 7, max = 7, message = "영업시간 리스트의 크기는 7과 같아야 합니다.")
		private List<String> breakTimeList; // 휴게시간 리스트

		@NotEmpty(message = "주문마감시간 리스트는 필수항목입니다.")
		@Size(min = 7, max = 7, message = "영업시간 리스트의 크기는 7과 같아야 합니다.")
		private List<String> lastOrderList; // 주문마감시간 리스트

		// 주문마감시간 없음으로 재설정
		public void resetLastOrder(int index) {
			lastOrderList.set(index, "없음");
		}
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class UpdateHolidaysReqDto {

		@NotNull(message = "정기휴무는 Null일 수 없습니다.") // 공백은 가능
		@Pattern(regexp = "([0-5][0-6]\\s){0,42}", message = "정기휴무 입력 형식이 올바르지 않습니다.")
		private String holidays; // 정기휴무 (형식: "10 11 ", ** 맨끝에 공백 필수!)
	}

	@AllArgsConstructor
	@Getter
	public static class UpdateMenuReqDto {

		private List<MultipartFile> addedImageFileList; // 추가된 메뉴사진 파일 리스트

		private List<String> deletedImageUrlList; // 삭제된 메뉴사진 URL 리스트

		// MenuImage 엔티티 생성
		public MenuImage toMenuImageEntity(String imageUrl, Store store) {
			return MenuImage.builder().imageUrl(imageUrl).store(store).build();
		}
	}

	@AllArgsConstructor
	@Getter
	public static class UpdateInsideReqDto {

		@NotNull(message = "전체테이블 수는 필수항목입니다.")
		@PositiveOrZero(message = "전체테이블 수는 0 이상의 숫자로 입력해 주세요.")
		@Max(value = 500, message = "전체테이블 최대 개수는 500개입니다.")
		private int allTableCount; // 전체테이블 수

		private List<MultipartFile> addedImageFileList; // 추가된 매장내부사진 파일 리스트

		private List<String> deletedImageUrlList; // 삭제 매장내부사진 URL 리스트

		// InsideImage 엔티티 생성
		public InsideImage toInsideImageEntity(String imageUrl, Store store) {
			return InsideImage.builder().imageUrl(imageUrl).store(store).build();
		}
	}

	@AllArgsConstructor
	@Getter
	public static class UpdateBasicReqDto {

		@NotBlank(message = "매장 전화번호는 필수항목입니다.")
		@Pattern(regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$", message = "매장 전화번호 형식이 올바르지 않습니다.")
		private String phone; // 전화번호

		@NotBlank(message = "매장 도로명주소는 필수항목입니다.")
		@Pattern(regexp = "^([가-힣]|\\d|[,-]|\\s){2,50}$", message = "매장 도로명주소 형식이 올바르지 않습니다.)")
		private String address; // 도로명주소

		@NotBlank(message = "매장 상세주소는 필수항목입니다.")
		@Pattern(regexp = "^([가-힣]|\\d|[,-]|\\s){2,50}$", message = "매장 상세주소 형식이 올바르지 않습니다.")
		private String detailAddress; // 상세주소

		@NotBlank(message = "매장 지번주소는 필수항목입니다.")
		@Pattern(regexp = "^([가-힣]|\\s){2,20}$", message = "매장 지번주소는 2~20자의 한글로 입력해 주세요.")
		private String jibunAddress; // 지번주소

		@NotNull(message = "매장 위치의 위도는 필수항목입니다.")
		private double latitude; // 위도

		@NotNull(message = "매장 위치의 경도는 필수항목입니다.")
		private double longitude; // 경도

		@NotBlank(message = "매장 소개는 필수항목입니다.")
		@Size(max = 500, message = "매장 소개는 500자 이내로 입력해 주세요.")
		private String description; // 매장 소개

		@NotNull(message = "웹사이트는 Null일 수 없습니다.") // 공백은 가능
		@Pattern(regexp = "(https?:\\/\\/)?(www\\.)?[\\w@:%.-_\\+~#=]{2,256}\\.[a-z]{2,6}\\b([\\w@:%-_\\+.~#?&//=]*)|()", message = "웹사이트 형식이 올바르지 않습니다.")
		private String website; // 웹사이트

		private List<MultipartFile> addedImageFileList; // 추가된 대표사진 파일 리스트

		private List<String> deletedImageUrlList; // 삭제된 대표사진 URL 리스트

		// BasicImage 오브젝트 리스트 생성
		public BasicImage toBasicImageEntity(String imageUrl, Store store) {
			return BasicImage.builder().imageUrl(imageUrl).store(store).build();
		}
	}

	@AllArgsConstructor
	@Getter
	public static class SpecificStoreReqDto {

		@NotBlank(message = "검색어 또는 카테고리는 필수항목입니다.")
		@Size(max = 50, message = "검색어 또는 카테고리는 50자 이내로 입력해 주세요.")
		private String data; // 검색어 또는 카테고리명

		@NotNull(message = "현재위치의 위도는 필수항목입니다.")
		private double latitude; // 현재위치 위도

		@NotNull(message = "현재위치의 경도는 필수항목입니다.")
		private double longitude; // 현재위치 경도
	}

	@Data
	public static class UpdateInfoReqDto {

		@NotEmpty(message = "매장정보 인덱스 리스트는 필수항목입니다.")
		@Size(max = 7, message = "매장정보 인덱스 리스트의 크기는 7 보다 작거나 같아야 합니다.")
		private List<Integer> infoIndexList; // 매장정보 인덱스 리스트
	}
}
