// package com.example.tablenow.service;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyIterable;
// import static org.mockito.Mockito.when;

// import java.io.FileInputStream;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.LocalTime;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.web.multipart.MultipartFile;

// import com.example.tablenow.domain.holidays.HolidaysRepository;
// import com.example.tablenow.domain.hours.Hours;
// import com.example.tablenow.domain.hours.HoursRepository;
// import com.example.tablenow.domain.notice.Notice;
// import com.example.tablenow.domain.notice.NoticeRepository;
// import com.example.tablenow.domain.notice_image.NoticeImage;
// import com.example.tablenow.domain.notice_image.NoticeImageRepository;
// import com.example.tablenow.domain.store.StoreRepository;
// import com.example.tablenow.domain.today_hours.TodayHours;
// import com.example.tablenow.domain.today_hours.TodayHoursRepository;
// import com.example.tablenow.service.notice.NoticeService;
// import com.example.tablenow.service.store.StoreCommonService;
// import com.example.tablenow.web.dto.notice.NoticeRespDto;
// import com.example.tablenow.web.dto.notice.SaveNoticeReqDto;
// import com.example.tablenow.web.dto.notice.SaveNoticeRespDto;
// import com.example.tablenow.web.dto.store.TodayHoursRespDto;

// // Repository를 제외한 Service만 테스트하기 위해 가짜데이터 생성
// // @Mock: 진짜 객체를 추상화된 가짜 객체로 만들어서 Mockito 환경에 주입

// @ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
// @ExtendWith(MockitoExtension.class) // 가짜 메모리 환경 생성
// public class NoticeServiceTest {

//     @InjectMocks // 가짜데이터 주입 받음
//     private NoticeService noticeService;

//     @Mock // 가짜 객체 생성
//     private NoticeRepository noticeRepository;

//     @Mock
//     private NoticeImageRepository noticeImageRepository;

//     @Mock
//     private StoreRepository storeRepository;

//     @Mock
//     private TodayHoursRepository todayHoursRepository;

//     @Mock
//     private HoursRepository hoursRepository;

//     @Mock
//     private HolidaysRepository holidaysRepository;

//     @Mock
//     private StoreCommonService commonService;

//     @Test
//     public void 알림등록() throws Exception {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;

//         LocalDate today = LocalDate.now();
//         String title = "알림제목";
//         String content = "알림내용";
//         String holidayStartDate = today.toString(); // 오늘 임시휴무
//         String holidayEndDate = today.toString(); // 오늘 임시휴무
//         String image = "test_image1.jpg";
//         List<MultipartFile> imageFileList = List.of(
//                 new MockMultipartFile("image1", image, "image/jpg",
//                         new FileInputStream(getClass().getResource("/static/" + image)
//                                 .getFile())));
//         SaveNoticeReqDto dto = new SaveNoticeReqDto(title, content, holidayStartDate, holidayEndDate,
//                 imageFileList, null);

//         // stub
//         // 가설 1. storeRepository의 mCheckStoreManager() 실행 시 1 리턴
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // 가설 2. noticeRepository의 save() 실행 시 Notice 엔티티 리턴
//         Notice notice = Notice.builder().title(title).content(content).holidayStartDate(holidayStartDate)
//                 .holidayEndDate(holidayEndDate).createdDate(LocalDateTime.now()).build();
//         when(noticeRepository.save(any())).thenReturn(notice);

//         // 가설 3. todayHoursRepository의 findByStoreId() 실행 시 TodayHours 엔티티 리턴
//         TodayHours todayHours = TodayHours.builder().holidayType(1).businessHours("10:00~22:00")
//                 .breakTime("15:00~17:00").lastOrder("21:30").today(today).build();
//         when(todayHoursRepository.findByStoreId(storeId)).thenReturn(todayHours);

//         // 가설 4. commonService의 makeImageUrl() 실행 시 문자열 리턴
//         when(commonService.makeImageUrl(any())).thenReturn(image);

//         // 가설 5. noticeImageRepository의 saveAll() 실행 시 List<NoticeImage> 타입 리턴
//         List<NoticeImage> noticeImageList = List.of(NoticeImage.builder().imageUrl(image).build());
//         when(noticeImageRepository.saveAll(anyIterable())).thenReturn(noticeImageList);

//         // when
//         SaveNoticeRespDto saveNoticeRespDto = noticeService.알림등록(storeId, userId, dto);

//         // then
//         assertThat(saveNoticeRespDto.getTitle()).isEqualTo(title);
//         assertThat(saveNoticeRespDto.getContent()).isEqualTo(content);
//         assertThat(saveNoticeRespDto.getHolidayStartDate()).isEqualTo(holidayStartDate);
//         assertThat(saveNoticeRespDto.getHolidayEndDate()).isEqualTo(holidayEndDate);
//         assertThat(LocalDateTime.parse(saveNoticeRespDto.getCreatedDate())).isBefore(LocalDateTime.now());
//         assertTrue(saveNoticeRespDto.getImageUrlList().contains(image));

//         // 영업일 -> 임시휴무 변경됨
//         assertThat(saveNoticeRespDto.getHolidayType()).isEqualTo(3);
//         assertThat(saveNoticeRespDto.getBusinessHours()).isEqualTo("없음");
//         assertThat(saveNoticeRespDto.getBreakTime()).isEqualTo("없음");
//         assertThat(saveNoticeRespDto.getLastOrder()).isEqualTo("없음");
//     }

//     @Test
//     public void 알림전체조회() {
//         // given
//         Long storeId = 1L;

//         // stub: noticeRepository의 findAllByStoreId() 실행 시 List<Notice> 타입 리턴
//         List<NoticeImage> noticeImageList = List.of(
//                 NoticeImage.builder().imageUrl("test_image1.jpg").build());
//         Notice notice = Notice.builder().title("알림제목").content("알림내용").holidayStartDate("")
//                 .holidayEndDate("").noticeImageList(noticeImageList).createdDate(LocalDateTime.now()).build();
//         List<Notice> noticeList = List.of(notice);
//         when(noticeRepository.findAllByStoreId(storeId)).thenReturn(noticeList);

//         // when
//         List<NoticeRespDto> noticeRespDtoList = noticeService.알림전체조회(storeId);

//         // then
//         NoticeRespDto noticeRespDto = noticeRespDtoList.get(0);
//         assertThat(noticeRespDto.getTitle()).isEqualTo(notice.getTitle());
//         assertThat(noticeRespDto.getContent()).isEqualTo(notice.getContent());
//         assertThat(noticeRespDto.getHolidayStartDate()).isEqualTo(notice.getHolidayStartDate());
//         assertThat(noticeRespDto.getHolidayEndDate()).isEqualTo(notice.getHolidayEndDate());
//         assertThat(LocalDateTime.parse(noticeRespDto.getCreatedDate())).isBefore(LocalDateTime.now());
//         assertTrue(noticeRespDto.getImageUrlList().contains(notice.getNoticeImageList().get(0).getImageUrl()));
//     }

//     @Test
//     public void 알림수정() throws Exception {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;
//         Long noticeId = 1L;

//         LocalDate today = LocalDate.now();
//         String title = "알림제목2";
//         String content = "알림내용2";
//         String holidayStartDate = ""; // 임시휴무 없음
//         String holidayEndDate = ""; // 임시휴무 없음
//         String newImage = "test_image2.jpg";
//         String deletedImage = "test_image1.jpg";
//         List<MultipartFile> imageFileList = List.of(
//                 new MockMultipartFile("image2", newImage, "image/jpg",
//                         new FileInputStream(getClass().getResource("/static/" + newImage)
//                                 .getFile())));
//         List<String> deletedImageUrlList = List.of(deletedImage);
//         SaveNoticeReqDto dto = new SaveNoticeReqDto(title, content, holidayStartDate, holidayEndDate, imageFileList,
//                 deletedImageUrlList);

//         // stub
//         // 가설 1. storeRepository의 mCheckStoreManager() 실행 시 1 리턴
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // 가설 2. noticeRepository의 findById() 실행 시 Optional<Notice> 타입 리턴
//         Notice notice = Notice.builder().id(noticeId).title("알림제목1").content("알림내용1").holidayStartDate(today.toString())
//                 .holidayEndDate(today.toString()).createdDate(LocalDateTime.now()).build();
//         when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

//         // 가설 3. todayHoursRepository의 findByStoreId() 실행 시 TodayHours 엔티티 리턴
//         TodayHours todayHours = TodayHours.builder().holidayType(3).businessHours("없음")
//                 .breakTime("없음").lastOrder("없음").today(LocalDate.now()).build();
//         when(todayHoursRepository.findByStoreId(storeId)).thenReturn(todayHours);

//         // 가설 4. hoursRepository의 findByDayTypeAndStoreId() 실행 시 Hours 엔티티 리턴
//         Hours hours = Hours.builder().dayType(0).businessHours("10:00~22:00").breakTime("15:00~17:00")
//                 .lastOrder("21:30").build();
//         when(hoursRepository.findByDayTypeAndStoreId(today.getDayOfWeek().getValue() - 1, storeId)).thenReturn(hours);

//         // 가설 5. commonService의 makeImageUrl() 실행 시 문자열 리턴
//         when(commonService.makeImageUrl(any())).thenReturn(newImage);

//         // 가설 6. noticeImageRepository의 saveAll() 실행 시 List<NoticeImage> 타입 리턴
//         List<NoticeImage> noticeImageList = List.of(NoticeImage.builder().imageUrl(newImage).build());
//         when(noticeImageRepository.saveAll(anyIterable())).thenReturn(noticeImageList);

//         // when
//         SaveNoticeRespDto saveNoticeRespDto = noticeService.알림수정(storeId, userId, noticeId, dto);

//         // then
//         assertThat(saveNoticeRespDto.getTitle()).isEqualTo(title);
//         assertThat(saveNoticeRespDto.getContent()).isEqualTo(content);
//         assertThat(saveNoticeRespDto.getHolidayStartDate()).isEqualTo(holidayStartDate);
//         assertThat(saveNoticeRespDto.getHolidayEndDate()).isEqualTo(holidayEndDate);
//         assertThat(LocalDateTime.parse(saveNoticeRespDto.getCreatedDate())).isBefore(LocalDateTime.now());
//         assertTrue(saveNoticeRespDto.getImageUrlList().contains(newImage));
//         assertFalse(saveNoticeRespDto.getImageUrlList().contains(deletedImage));

//         // 임시휴무 -> 영업일 변경됨
//         assertThat(saveNoticeRespDto.getHolidayType()).isEqualTo(1);
//         assertThat(saveNoticeRespDto.getBusinessHours()).isEqualTo(hours.getBusinessHours());
//         assertThat(saveNoticeRespDto.getBreakTime()).isEqualTo(hours.getBreakTime());
//         assertThat(saveNoticeRespDto.getLastOrder()).isEqualTo(hours.getLastOrder());
//     }

//     @Test
//     public void 알림삭제() {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;
//         Long noticeId = 1L;

//         // stub
//         // 가설 1. storeRepository의 mCheckStoreManager() 실행 시 1 리턴
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // 가설 2. noticeRepository의 findById() 실행 시 Optional<Notice> 타입 리턴
//         LocalDate today = LocalDate.now();
//         Notice notice = Notice.builder().id(noticeId).title("알림제목").content("알림내용").holidayStartDate(today.toString())
//                 .holidayEndDate(today.toString()).createdDate(LocalDateTime.now()).build();
//         when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

//         // 가설 3. todayHoursRepository의 findByStoreId() 실행 시 TodayHours 엔티티 리턴
//         TodayHours todayHours = TodayHours.builder().holidayType(3).businessHours("없음")
//                 .breakTime("없음").lastOrder("없음").today(LocalDate.now()).build();
//         when(todayHoursRepository.findByStoreId(storeId)).thenReturn(todayHours);

//         // 가설 4. holidaysRepository의 mFindHolidaysByStoreId() 실행 시 공백 문자열 리턴
//         when(holidaysRepository.mFindHolidaysByStoreId(storeId)).thenReturn("");

//         // 가설 5. hoursRepository의 findByDayTypeAndStoreId() 실행 시 Hours 엔티티 리턴
//         int dayOfWeek = today.getDayOfWeek().getValue() - 1; // 오늘 요일
//         Hours hours = Hours.builder().dayType(dayOfWeek).businessHours("10:00~22:00").breakTime("15:00~17:00")
//                 .lastOrder("21:30").build();
//         when(hoursRepository.findByDayTypeAndStoreId(dayOfWeek, storeId)).thenReturn(hours);

//         // 가설 6. noticeImageRepository의 mFindAllImageUrlByNoticeId() 실행 시 List<String> 타입 리턴
//         when(noticeImageRepository.mFindAllImageUrlByNoticeId(noticeId)).thenReturn(List.of());

//         // 가설 7. commonService의 setState() 실행 시 영업중 리턴
//         String now = LocalTime.now().toString().substring(0, 5);
//         System.out.println("현재 시간: " + now);
//         when(commonService.setState(LocalTime.now().toString().substring(0, 5), 1, hours.getBusinessHours(),
//                 hours.getBreakTime(), hours.getLastOrder())).thenReturn("영업중");

//         // when
//         TodayHoursRespDto todayHoursRespDto = noticeService.알림삭제(storeId, userId, noticeId);

//         // then
//         assertThat(todayHoursRespDto).isNotNull();

//         // 임시휴무 -> 영업일 변경됨
//         assertThat(todayHoursRespDto.getHolidayType()).isEqualTo(1);
//         assertThat(todayHoursRespDto.getBusinessHours()).isEqualTo(hours.getBusinessHours());
//         assertThat(todayHoursRespDto.getBreakTime()).isEqualTo(hours.getBreakTime());
//         assertThat(todayHoursRespDto.getLastOrder()).isEqualTo(hours.getLastOrder());
//         System.out.println("영업상태: " + todayHoursRespDto.getState());
//     }
// }
