package com.example.tablenow.web.dto.notice;

import java.util.List;

import com.example.tablenow.domain.notice.Notice;
import com.example.tablenow.domain.notice_image.NoticeImage;
import com.example.tablenow.domain.today_hours.TodayHours;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class NoticeRespDto {

	private Long id; // 알림 id
	private String title; // 제목
	private String content; // 내용
	private String holidayStartDate; // 임시휴무 시작 날짜
	private String holidayEndDate; // 임시휴무 종료 날짜
	private String createdDate; // 등록일
	private List<String> imageUrlList; // 첨부사진 리스트

	// Notice, NoticeImage 엔티티를 바탕으로 NoticeRespDto 객체 생성
	public NoticeRespDto(Notice notice, List<NoticeImage> noticeImageList) {
		id = notice.getId();
		title = notice.getTitle();
		content = notice.getContent();
		holidayStartDate = notice.getHolidayStartDate();
		holidayEndDate = notice.getHolidayEndDate();
		createdDate = notice.getCreatedDate().toString();
		imageUrlList = noticeImageList.stream().map(noticeImage -> noticeImage.getImageUrl()).toList();
	}

	@AllArgsConstructor
	@Getter
	public static class NoticeListRespDto {

		private List<NoticeRespDto> items;
	}

	@Getter
	public static class SaveNoticeRespDto {

		private Long id; // 알림 id
		private String title; // 제목
		private String content; // 내용
		private String holidayStartDate; // 임시휴무 시작 날짜
		private String holidayEndDate; // 임시휴무 종료 날짜
		private String createdDate; // 등록일
		private List<String> imageUrlList; // 첨부사진 리스트

		private int holidayType; // 휴무 여부 (1 영업일, 2 정기휴무, 3 알림등록 임시휴무, 4 임의변경 임시휴무)
		private String businessHours; // 오늘의 영업시간
		private String breakTime; // 오늘의 휴게시간
		private String lastOrder; // 오늘의 주문마감시간

		private String state; // 영업상태

		// Notice, NoticeImage, TodayHours 엔티티를 바탕으로 SaveNoticeRespDto 객체 생성
		public SaveNoticeRespDto(Notice notice, List<NoticeImage> noticeImageList, TodayHours todayHours,
				String state) {
			id = notice.getId();
			title = notice.getTitle();
			content = notice.getContent();
			holidayStartDate = notice.getHolidayStartDate();
			holidayEndDate = notice.getHolidayEndDate();
			createdDate = notice.getCreatedDate().toString();
			imageUrlList = noticeImageList.stream().map(noticeImage -> noticeImage.getImageUrl()).toList();

			holidayType = todayHours.getHolidayType();
			businessHours = todayHours.getBusinessHours();
			breakTime = todayHours.getBreakTime();
			lastOrder = todayHours.getLastOrder();
			this.state = state;
		}
	}
}