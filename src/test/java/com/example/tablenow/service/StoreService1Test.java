// package com.example.tablenow.service;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyIterable;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.when;

// import java.io.FileInputStream;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.LocalTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import javax.persistence.EntityManager;
// import javax.persistence.EntityManagerFactory;
// import javax.persistence.EntityTransaction;
// import javax.persistence.Persistence;
// import javax.persistence.Query;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.qlrm.mapper.JpaResultMapper;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.web.multipart.MultipartFile;

// import com.example.tablenow.domain.basic_image.BasicImage;
// import com.example.tablenow.domain.basic_image.BasicImageRepository;
// import com.example.tablenow.domain.holidays.Holidays;
// import com.example.tablenow.domain.holidays.HolidaysRepository;
// import com.example.tablenow.domain.hours.Hours;
// import com.example.tablenow.domain.hours.HoursRepository;
// import com.example.tablenow.domain.inside_image.InsideImage;
// import com.example.tablenow.domain.inside_image.InsideImageRepository;
// import com.example.tablenow.domain.menu_image.MenuImage;
// import com.example.tablenow.domain.menu_image.MenuImageRepository;
// import com.example.tablenow.domain.notice.Notice;
// import com.example.tablenow.domain.notice.NoticeRepository;
// import com.example.tablenow.domain.notice_image.NoticeImageRepository;
// import com.example.tablenow.domain.store.Store;
// import com.example.tablenow.domain.store.StoreRepository;
// import com.example.tablenow.domain.tables.Tables;
// import com.example.tablenow.domain.tables.TablesRepository;
// import com.example.tablenow.domain.today_hours.TodayHours;
// import com.example.tablenow.domain.today_hours.TodayHoursRepository;
// import com.example.tablenow.domain.user.User;
// import com.example.tablenow.service.store.StoreCommonService;
// import com.example.tablenow.service.store.StoreService1;
// import com.example.tablenow.web.dto.store.BasicRespDto;
// import com.example.tablenow.web.dto.store.CheckExistReqDto;
// import com.example.tablenow.web.dto.store.DeleteStoreRespDto;
// import com.example.tablenow.web.dto.store.HolidaysRespDto;
// import com.example.tablenow.web.dto.store.HoursRespDto;
// import com.example.tablenow.web.dto.store.InsideRespDto;
// import com.example.tablenow.web.dto.store.MenuRespDto;
// import com.example.tablenow.web.dto.store.MyStoreRespDto;
// import com.example.tablenow.web.dto.store.SaveStoreReqDto;
// import com.example.tablenow.web.dto.store.SaveStoreRespDto;
// import com.example.tablenow.web.dto.store.TablesRespDto;
// import com.example.tablenow.web.dto.store.TodayHoursRespDto;
// import com.example.tablenow.web.dto.store.UpdateBasicReqDto;
// import com.example.tablenow.web.dto.store.UpdateBasicRespDto;
// import com.example.tablenow.web.dto.store.UpdateHolidaysRespDto;
// import com.example.tablenow.web.dto.store.UpdateHoursReqDto;
// import com.example.tablenow.web.dto.store.UpdateHoursRespDto;
// import com.example.tablenow.web.dto.store.UpdateInsideReqDto;
// import com.example.tablenow.web.dto.store.UpdateInsideRespDto;
// import com.example.tablenow.web.dto.store.UpdateMenuReqDto;
// import com.example.tablenow.web.dto.store.UpdateMenuRespDto;
// import com.example.tablenow.web.dto.store.UpdateTablesReqDto;
// import com.example.tablenow.web.dto.store.UpdateTodayHoursReqDto;

// @ActiveProfiles("dev") // dev ??????????????? ??????????????? ??????
// @ExtendWith(MockitoExtension.class) // ?????? ????????? ?????? ??????
// public class StoreService1Test {

//     @InjectMocks // ??????????????? ?????? ??????
//     private StoreService1 storeService1;

//     @Mock // ?????? ?????? ??????
//     private StoreCommonService commonService;

//     @Mock
//     private StoreRepository storeRepository;

//     @Mock
//     private BasicImageRepository basicImageRepository;

//     @Mock
//     private InsideImageRepository insideImageRepository;

//     @Mock
//     private MenuImageRepository menuImageRepository;

//     @Mock
//     private HolidaysRepository holidaysRepository;

//     @Mock
//     private HoursRepository hoursRepository;

//     @Mock
//     private TablesRepository tablesRepository;

//     @Mock
//     private TodayHoursRepository todayHoursRepository;

//     @Mock
//     private NoticeRepository noticeRepository;

//     @Mock
//     private NoticeImageRepository noticeImageRepository;

//     // resources/META-INF/persistence.xml H2 ?????? ?????? (?????????????????? ????????????)
//     final String PERSISTENCE_UNIT_NAME = "jpa";
//     EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

//     User user1;
//     Store store1;
//     Store store2;
//     BasicImage basicImage1;
//     BasicImage basicImage2;
//     TodayHours todayHours1;
//     TodayHours todayHours2;

//     @Test
//     public void ??????????????????() {
//         // given
//         CheckExistReqDto dto = new CheckExistReqDto("?????????", "????????????", "02-1111-1111", "???????????????");

//         // stub: storeRepository??? mCheckExist() ?????? ??? 0 ??????
//         when(storeRepository.mCheckExist(dto.getName(), dto.getCategory(), dto.getPhone(), dto.getAddress()))
//                 .thenReturn(0);

//         // when
//         int result = storeService1.??????????????????(dto);

//         // then
//         assertThat(result).isEqualTo(0);
//     }

//     @Test
//     public void ????????????() throws Exception {
//         // given
//         User user = User.builder().id(1L).username("tablenow").password("tablenow1!").name("?????????").phone("01011111111")
//                 .uniqueKey("uniqueKey").email("tablenow@naver.com").build();

//         String image = "test_image1.jpg";
//         List<MultipartFile> imageFileList = List.of(
//                 new MockMultipartFile("image1", image, "image/jpg",
//                         new FileInputStream(getClass().getResource("/static/" + image)
//                                 .getFile())));
//         List<String> businessHoursList = List.of("10:00~22:00", "10:00~22:00", "10:00~22:00", "10:00~22:00",
//                 "10:00~22:00", "10:00~22:00", "10:00~22:00");
//         List<String> breakTimeList = List.of("15:00~17:00", "15:00~17:00", "15:00~17:00", "15:00~17:00", "15:00~17:00",
//                 "15:00~17:00", "15:00~17:00");
//         List<String> lastOrderList = List.of("21:30", "21:30", "21:30", "21:30", "21:30", "21:30", "21:30");
//         SaveStoreReqDto dto = new SaveStoreReqDto("?????????", "????????????", "02-1111-1111", "???????????????", "????????????", "????????????", 37.1, 127.1, "????????????",
//                 "", 10, imageFileList, imageFileList, imageFileList, "00 10 20 30 40 50", businessHoursList,
//                 breakTimeList, lastOrderList);

//         // stub
//         // ?????? 1. storeRepository??? save() ?????? ??? Store ????????? ??????
//         Store store = Store.builder().id(1L).name("?????????").category("????????????").phone("02-1111-1111").address("???????????????")
//                 .jibunAddress("????????????").latitude(37.1).longitude(127.1).description("????????????").website("")
//                 .build();
//         when(storeRepository.save(any())).thenReturn(store);

//         // ?????? 2. commonService??? makeImageUrl() ?????? ??? ????????? ??????
//         when(commonService.makeImageUrl(any(MultipartFile.class))).thenReturn(image);

//         // ?????? 3. basicImageRepository??? saveAll() ?????? ??? BasicImage ????????? ????????? ??????
//         List<BasicImage> basicImageList = List.of(BasicImage.builder().imageUrl(image).build());
//         when(basicImageRepository.saveAll(anyIterable())).thenReturn(basicImageList);

//         // ?????? 4. insideImageRepository??? saveAll() ?????? ??? InsideImage ????????? ????????? ??????
//         List<InsideImage> insideImageList = List.of(InsideImage.builder().imageUrl(image).build());
//         when(insideImageRepository.saveAll(anyIterable())).thenReturn(insideImageList);

//         // ?????? 5. menuImageRepository??? saveAll() ?????? ??? MenuImage ????????? ????????? ??????
//         List<MenuImage> menuImageList = List.of(MenuImage.builder().imageUrl(image).build());
//         when(menuImageRepository.saveAll(anyIterable())).thenReturn(menuImageList);

//         // ?????? 6. holidaysRepository??? save() ?????? ??? Holidays ????????? ??????
//         Holidays holidays = Holidays.builder().holidays("00 10 20 30 40 50").build();
//         when(holidaysRepository.save(any())).thenReturn(holidays);

//         // ?????? 7. hoursRepository??? saveAll() ?????? ??? Hours ????????? ????????? ??????
//         List<Hours> hoursList = new ArrayList<>();
//         for (int i = 0; i < 7; i++) {
//             hoursList.add(Hours.builder().dayType(i).businessHours("10:00~22:00").breakTime("15:00~17:00")
//                     .lastOrder("21:30").build());
//         }
//         when(hoursRepository.saveAll(anyIterable())).thenReturn(hoursList);

//         // ?????? 8. tablesRepository??? save() ?????? ??? Tables ????????? ??????
//         Tables tables = Tables.builder().allTableCount(10).tableCount(10).paused(false).build();
//         when(tablesRepository.save(any())).thenReturn(tables);

//         // ?????? 9. todayHoursRepository??? save() ?????? ??? TodayHours ????????? ??????
//         TodayHours todayHours = TodayHours.builder().holidayType(1).businessHours("10:00~22:00")
//                 .breakTime("15:00~17:00").lastOrder("21:30").today(LocalDate.now()).build();
//         when(todayHoursRepository.save(any())).thenReturn(todayHours);

//         // ?????? 10. commonService??? ???????????????????????????_????????????() ?????? ??? TodayHours ??????
//         when(commonService.???????????????????????????_????????????(LocalDate.now(), store.getId())).thenReturn(todayHours);

//         // when
//         SaveStoreRespDto saveStoreRespDto = storeService1.????????????(user, dto);

//         // then
//         assertThat(saveStoreRespDto.getName()).isEqualTo(dto.getName()); // Store ?????????
//         assertTrue(saveStoreRespDto.getBasicImageUrlList().contains(basicImageList.get(0).getImageUrl())); // BasicImage
//                                                                                                            // ?????????
//         assertTrue(saveStoreRespDto.getInsideImageUrlList().contains(insideImageList.get(0).getImageUrl())); // InsideImage
//                                                                                                              // ?????????
//         assertTrue(saveStoreRespDto.getMenuImageUrlList().contains(menuImageList.get(0).getImageUrl())); // MenuImage
//                                                                                                          // ?????????
//         assertThat(saveStoreRespDto.getHolidays()).isEqualTo(dto.getHolidays()); // Holidays ?????????
//         assertThat(saveStoreRespDto.getBusinessHoursList()).isEqualTo(dto.getBusinessHoursList()); // Hours ?????????
//         assertThat(saveStoreRespDto.getAllTableCount()).isEqualTo(dto.getAllTableCount()); // Tables ?????????
//         assertThat(saveStoreRespDto.getToday()).isEqualTo(todayHours.getToday().toString()); // TodayHours ?????????
//     }

//     @Test
//     public void ????????????????????????() {
//         // given
//         ??????????????????_???????????????();
//         Long userId = user1.getId();

//         // stub

//         // when
//         EntityManager em = emf.createEntityManager();
//         String sql = "SELECT s.id, s.name, s.address, s.phone, b.imageUrl AS basicImageUrl, "
//                 + "t.today, t.holidayType, t.businessHours, t.breakTime, t.lastOrder "
//                 + "FROM store s, basic_image b, today_hours t "
//                 + "WHERE s.userId = :userId AND b.storeId = s.id AND t.storeId = s.id GROUP BY s.id";
//         Query query = em.createNativeQuery(sql).setParameter("userId", userId);
//         JpaResultMapper result = new JpaResultMapper();
//         List<MyStoreRespDto> myStoreList = result.list(query, MyStoreRespDto.class);

//         // then
//         MyStoreRespDto myStore = myStoreList.get(0);
//         assertThat(myStore.getName()).isEqualTo(store1.getName());
//         assertThat(myStore.getBasicImageUrl()).isEqualTo(basicImage1.getImageUrl());
//         assertThat(myStore.getHolidayType()).isEqualTo(todayHours1.getHolidayType());

//         em.close();
//     }

//     @Test
//     public void ????????????1?????????() {
//         // given
//         ??????????????????_???????????????();
//         Long storeId = store1.getId();

//         // stub

//         // when
//         EntityManager em = emf.createEntityManager();
//         String sql = "SELECT s.id, s.name, s.address, s.phone, b.imageUrl AS basicImageUrl, "
//                 + "t.today, t.holidayType, t.businessHours, t.breakTime, t.lastOrder "
//                 + "FROM store s, basic_image b, today_hours t "
//                 + "WHERE s.id = :storeId AND b.storeId = s.id AND t.storeId = s.id GROUP BY s.id";
//         Query query = em.createNativeQuery(sql).setParameter("storeId", storeId);
//         JpaResultMapper result = new JpaResultMapper();
//         MyStoreRespDto myStore = result.uniqueResult(query, MyStoreRespDto.class);

//         // then
//         assertThat(myStore.getName()).isEqualTo(store1.getName());
//         assertThat(myStore.getBasicImageUrl()).isEqualTo(basicImage1.getImageUrl());
//         assertThat(myStore.getHolidayType()).isEqualTo(todayHours1.getHolidayType());

//         em.close();
//     }

//     @Test
//     public void ??????????????????() {
//         // given
//         Long storeId = 1L;

//         // stub: hoursRepository??? findAllByStoreId() ?????? ??? Hours ????????? ????????? ??????
//         List<Hours> hoursList = new ArrayList<>();
//         LocalDateTime modifiedDate = LocalDateTime.now();
//         for (int i = 0; i < 7; i++) {
//             hoursList.add(Hours.builder().dayType(i).businessHours("9:00~21:00").breakTime("12:00~16:00")
//                     .lastOrder("20:30").modifiedDate(modifiedDate).build());
//         }
//         when(hoursRepository.findAllByStoreId(storeId)).thenReturn(hoursList);

//         // when
//         HoursRespDto hoursRespDto = storeService1.??????????????????(storeId);

//         // then
//         assertThat(hoursRespDto.getRun24HoursList()[0]).isEqualTo(false);
//         assertThat(hoursRespDto.getHasBreakTimeList()[0]).isEqualTo(true);
//         assertThat(hoursRespDto.getHasLastOrderList()[0]).isEqualTo(true);
//         assertThat(hoursRespDto.getModifiedDate()).isEqualTo(modifiedDate.toString());
//     }

//     @Test
//     public void ??????????????????() {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;

//         UpdateHoursReqDto dto = new UpdateHoursReqDto(
//                 new String[] { "9:00", "9:00", "9:00", "9:00", "9:00", "9:00", "9:00" },
//                 new String[] { "21:00", "21:00", "21:00", "21:00", "21:00", "21:00", "21:00" },
//                 new boolean[] { false, false, false, false, false, false, false },
//                 new String[] { "", "", "", "", "", "", "" },
//                 new String[] { "", "", "", "", "", "", "" },
//                 new boolean[] { false, false, false, false, false, false, false },
//                 new String[] { "", "", "", "", "", "", "" });

//         // stub
//         // ?????? 1. storeRepository??? mCheckStoreManager() ?????? ??? 1 ??????
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // ?????? 2. hoursRepository??? findAllByStoreId() ?????? ??? Hours ????????? ????????? ??????
//         List<Hours> hoursList = new ArrayList<>();
//         LocalDateTime modifiedDate = LocalDateTime.now();
//         for (int i = 0; i < 7; i++) {
//             hoursList.add(Hours.builder().dayType(i).businessHours("10:00~22:00").breakTime("15:00~17:00")
//                     .lastOrder("21:30").modifiedDate(modifiedDate).build());
//         }
//         when(hoursRepository.findAllByStoreId(storeId)).thenReturn(hoursList);

//         // ?????? 3. todayHoursRepository??? findByStoreId() ?????? ??? TodayHours ????????? ?????? (?????? ?????????)
//         TodayHours todayHours = TodayHours.builder().holidayType(1).businessHours("10:00~22:00")
//                 .breakTime("15:00~17:00").lastOrder("21:30").today(LocalDate.now()).build();
//         when(todayHoursRepository.findByStoreId(storeId)).thenReturn(todayHours);

//         // ?????? 4. commonService??? setState() ?????? ??? ????????? ??????
//         when(commonService.setState(LocalTime.now().toString().substring(0, 5), 1, "9:00~21:00", "??????", "??????"))
//                 .thenReturn("?????????");

//         // when
//         UpdateHoursRespDto updateHoursRespDto = storeService1.??????????????????(storeId, userId, dto);

//         // then
//         assertTrue(updateHoursRespDto.getBusinessHoursList().contains("9:00~21:00")); // Hours ?????????
//         assertThat(updateHoursRespDto.getHolidayType()).isEqualTo(1);
//         assertThat(updateHoursRespDto.getBusinessHours()).isEqualTo("9:00~21:00"); // 10:00~22:00 -> 9:00~21:00 ?????????
//         assertThat(updateHoursRespDto.getBreakTime()).isEqualTo("??????"); // 15:00~17:00 -> ?????? ?????????
//         assertThat(updateHoursRespDto.getLastOrder()).isEqualTo("??????"); // 21:30 -> ?????? ?????????
//         assertThat(updateHoursRespDto.getState()).isEqualTo("?????????");
//     }

//     @Test
//     public void ??????????????????() {
//         // given
//         Long storeId = 1L;

//         // stub: holidaysRepository??? findByStoreId() ?????? ??? Holidays ????????? ??????
//         String holidays = "00 10 20 30 40 50"; // ?????? ????????? ??????
//         Holidays holidaysEntity = Holidays.builder().holidays(holidays).modifiedDate(LocalDateTime.now()).build();
//         when(holidaysRepository.findByStoreId(storeId)).thenReturn(holidaysEntity);

//         // when
//         HolidaysRespDto holidaysRespDto = storeService1.??????????????????(storeId);

//         // then
//         assertThat(holidaysRespDto.getHolidays()).isEqualTo(holidays);
//     }

//     @Test
//     public void ??????????????????() {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;
//         String newHolidays = ""; // ???????????? ??????

//         // stub
//         // ?????? 1. storeRepository??? mCheckStoreManager() ?????? ??? 1 ??????
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // ?????? 2. holidaysRepository??? findByStoreId() ?????? ??? Holidays ????????? ??????
//         Holidays holidaysEntity = Holidays.builder().holidays("00 10 20 30 40 50").modifiedDate(LocalDateTime.now())
//                 .build();
//         when(holidaysRepository.findByStoreId(storeId)).thenReturn(holidaysEntity);

//         // ?????? 3. todayHoursRepository??? findByStoreId() ?????? ??? TodayHours ????????? ?????? (????????? ??????
//         // ????????????)
//         TodayHours todayHours = TodayHours.builder().holidayType(2).businessHours("??????")
//                 .breakTime("??????").lastOrder("??????").today(LocalDate.now()).build();
//         when(todayHoursRepository.findByStoreId(storeId)).thenReturn(todayHours);

//         // ?????? 4. noticeRepository??? findAllByStoreId() ?????? ??? Notice ????????? ????????? ?????? (?????? ????????????
//         // ??????)
//         Notice notice = Notice.builder().title("?????? ??????").title("?????? ??????").holidayStartDate("").holidayEndDate("")
//                 .build();
//         when(noticeRepository.findAllByStoreId(storeId)).thenReturn(List.of(notice));

//         // ?????? 5. hoursRepository??? findByDayTypeAndStoreId() ?????? ??? Hours ????????? ?????? (?????? ?????????)
//         int dayOfWeek = LocalDate.now().getDayOfWeek().getValue() - 1;
//         Hours hours = Hours.builder().dayType(dayOfWeek).businessHours("10:00~22:00")
//                 .breakTime("15:00~17:00").lastOrder("21:30").build();
//         when(hoursRepository.findByDayTypeAndStoreId(dayOfWeek, storeId)).thenReturn(hours);

//         // ?????? 6. commonService??? setState() ?????? ??? ????????? ??????
//         when(commonService.setState(LocalTime.now().toString().substring(0, 5), 1, hours.getBusinessHours(), hours.getBreakTime(), hours.getLastOrder()))
//                 .thenReturn("?????????");

//         // when
//         // ?????? ???????????? -> ???????????? ??????
//         UpdateHolidaysRespDto updateHolidaysRespDto = storeService1.??????????????????(storeId, userId, newHolidays);

//         // then
//         assertThat(updateHolidaysRespDto.getHolidays()).isEqualTo(newHolidays);
//         assertThat(updateHolidaysRespDto.getHolidayType()).isEqualTo(1);
//         assertThat(updateHolidaysRespDto.getBusinessHours()).isEqualTo(hours.getBusinessHours());
//         assertThat(updateHolidaysRespDto.getBreakTime()).isEqualTo(hours.getBreakTime());
//         assertThat(updateHolidaysRespDto.getLastOrder()).isEqualTo(hours.getLastOrder());
//         assertThat(updateHolidaysRespDto.getState()).isEqualTo("?????????");
//     }

//     @Test
//     public void ????????????() {
//         // given
//         Long storeId = 1L;

//         // stub: menuImageRepository??? findAllByStoreId() ?????? ??? MenuImage ????????? ????????? ??????
//         LocalDateTime createdDate = LocalDateTime.now();
//         List<MenuImage> menuImageList = List
//                 .of(MenuImage.builder().imageUrl("test_image1.jpg").createdDate(createdDate).build());
//         when(menuImageRepository.findAllByStoreId(storeId)).thenReturn(menuImageList);

//         // when
//         MenuRespDto menuRespDto = storeService1.????????????(storeId);

//         // then
//         assertTrue(menuRespDto.getImageUrlList().contains(menuImageList.get(0).getImageUrl()));
//         assertThat(menuRespDto.getModifiedDate()).isEqualTo(createdDate.toString());
//     }

//     @Test
//     public void ????????????() throws Exception {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;

//         String newImage = "test_image2.jpg";
//         String deletedImage = "test_image1.jpg";
//         List<MultipartFile> imageFileList = List.of(
//                 new MockMultipartFile("newImage", newImage, "image/jpg",
//                         new FileInputStream(getClass().getResource("/static/" + newImage)
//                                 .getFile())));
//         List<String> deletedImageList = List.of(deletedImage);
//         UpdateMenuReqDto dto = new UpdateMenuReqDto(imageFileList, deletedImageList);

//         // stub
//         // ?????? 1. storeRepository??? mCheckStoreManager() ?????? ??? 1 ??????
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // ?????? 2. commonService??? makeImageUrl() ?????? ??? ????????? ??????
//         when(commonService.makeImageUrl(any(MultipartFile.class))).thenReturn(newImage);

//         // ?????? 3. menuImageRepository??? saveAll() ?????? ??? MenuImage ????????? ????????? ??????
//         List<MenuImage> menuImageList = List.of(MenuImage.builder().imageUrl(newImage).build());
//         when(menuImageRepository.saveAll(anyIterable())).thenReturn(menuImageList);

//         // when
//         UpdateMenuRespDto updateMenuRespDto = storeService1.????????????(storeId, userId, dto);

//         // then
//         assertTrue(updateMenuRespDto.getMenuImageUrlList().contains(menuImageList.get(0).getImageUrl()));
//         assertThat(updateMenuRespDto.getDeletedCount()).isEqualTo(deletedImageList.size());
//     }

//     @Test
//     public void ????????????????????????() {
//         // given
//         Long storeId = 1L;

//         // stub
//         // ?????? 1. tablesRepository??? mFindAllTableCountByStoreId() ?????? ??? 10 ??????
//         int allTableCount = 10;
//         when(tablesRepository.mFindAllTableCountByStoreId(storeId)).thenReturn(allTableCount);

//         // ?????? 2. insideImageRepository??? findAllByStoreId() ?????? ??? InsideImage ????????? ????????? ??????
//         List<InsideImage> insideImageList = List.of(InsideImage.builder().imageUrl("test_image1.jpg").build());
//         when(insideImageRepository.findAllByStoreId(storeId)).thenReturn(insideImageList);

//         // when
//         InsideRespDto insideRespDto = storeService1.????????????????????????(storeId);

//         // then
//         assertThat(insideRespDto.getAllTableCount()).isEqualTo(allTableCount);
//         assertTrue(insideRespDto.getImageUrlList().contains(insideImageList.get(0).getImageUrl()));
//     }

//     @Test
//     public void ????????????????????????() throws Exception {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;

//         int allTableCount = 5;
//         String newImage = "test_image2.jpg";
//         String deletedImage = "test_image1.jpg";
//         List<MultipartFile> imageFileList = List.of(
//                 new MockMultipartFile("newImage", newImage, "image/jpg",
//                         new FileInputStream(getClass().getResource("/static/" + newImage)
//                                 .getFile())));
//         List<String> deletedImageList = List.of(deletedImage);
//         UpdateInsideReqDto dto = new UpdateInsideReqDto(allTableCount, imageFileList, deletedImageList);

//         // stub
//         // ?????? 1. storeRepository??? mCheckStoreManager() ?????? ??? 1 ??????
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // ?????? 2. tablesRepository??? findByStoreId() ?????? ??? Tables ????????? ??????
//         Tables tables = Tables.builder().allTableCount(10).tableCount(10).paused(false).build();
//         when(tablesRepository.findByStoreId(storeId)).thenReturn(tables);

//         // ?????? 3. commonService??? makeImageUrl() ?????? ??? ????????? ??????
//         when(commonService.makeImageUrl(any(MultipartFile.class))).thenReturn(newImage);

//         // ?????? 4. insideImageRepository??? saveAll() ?????? ??? InsideImage ????????? ????????? ??????
//         List<InsideImage> insideImageList = List.of(InsideImage.builder().imageUrl(newImage).build());
//         when(insideImageRepository.saveAll(anyIterable())).thenReturn(insideImageList);

//         // when
//         UpdateInsideRespDto updateInsideRespDto = storeService1.????????????????????????(storeId, userId, dto);

//         // then
//         // ??????????????? ??? 10 -> 5 ??????
//         assertThat(updateInsideRespDto.getAllTableCount()).isEqualTo(allTableCount);
//         assertThat(updateInsideRespDto.getTableCount()).isEqualTo(allTableCount);
//         assertTrue(updateInsideRespDto.getInsideImageUrlList().contains(insideImageList.get(0).getImageUrl()));
//         assertThat(updateInsideRespDto.getDeletedCount()).isEqualTo(deletedImageList.size());
//     }

//     @Test
//     public void ??????????????????() {
//         // given
//         ??????????????????_???????????????();
//         Long storeId = store1.getId();

//         // stub

//         // when
//         EntityManager em = emf.createEntityManager();
//         String sql = "SELECT id, name, category, phone, address, jibunAddress, latitude, longitude, description, website "
//                 + "FROM store WHERE id = ?";
//         Query query = em.createNativeQuery(sql).setParameter(1, storeId);
//         JpaResultMapper result = new JpaResultMapper();
//         BasicRespDto dto = result.uniqueResult(query, BasicRespDto.class);

//         // then
//         assertThat(dto.getName()).isEqualTo(store1.getName());
//     }

//     @Test
//     public void ??????????????????() throws Exception {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;

//         String newImage = "test_image2.jpg";
//         String deletedImage = "test_image1.jpg";
//         List<MultipartFile> imageFileList = List.of(
//                 new MockMultipartFile("newImage", newImage, "image/jpg",
//                         new FileInputStream(getClass().getResource("/static/" + newImage)
//                                 .getFile())));
//         List<String> deletedImageList = List.of(deletedImage);
//         UpdateBasicReqDto dto = new UpdateBasicReqDto("02-2222-2222", "???????????????2", "????????????2", "????????????2", 37.2, 127.2, "????????????2", "",
//                 imageFileList, deletedImageList);

//         // stub
//         // ?????? 1. storeRepository??? mCheckStoreManager() ?????? ??? 1 ??????
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // ?????? 2. storeRepository??? findById() ?????? ??? Store ????????? ??????
//         Store store = Store.builder().name("?????????1").category("????????????1").phone("02-1111-1111").address("???????????????1")
//                 .jibunAddress("????????????1").latitude(37.1).longitude(127.1).description("????????????1").website("")
//                 .build();
//         when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

//         // ?????? 3. commonService??? makeImageUrl() ?????? ??? ????????? ??????
//         when(commonService.makeImageUrl(any(MultipartFile.class))).thenReturn(newImage);

//         // ?????? 4. basicImageRepository??? saveAll() ?????? ??? BasicImage ????????? ????????? ??????
//         List<BasicImage> basicImageList = List.of(BasicImage.builder().imageUrl(newImage).build());
//         when(basicImageRepository.saveAll(anyIterable())).thenReturn(basicImageList);

//         // ?????? 5. basicImageRepository??? mFindOneImageUrlByStoreId() ?????? ??? ????????? ??????
//         when(basicImageRepository.mFindOneImageUrlByStoreId(storeId)).thenReturn(newImage);

//         // when
//         UpdateBasicRespDto updateBasicRespDto = storeService1.??????????????????(storeId, userId, dto);

//         // then
//         assertThat(updateBasicRespDto.getName()).isEqualTo(store.getName());
//         assertThat(updateBasicRespDto.getPhone()).isEqualTo(dto.getPhone());
//         assertThat(updateBasicRespDto.getBasicImageUrl()).isEqualTo(newImage);
//         assertTrue(updateBasicRespDto.getBasicImageUrlList().contains(basicImageList.get(0).getImageUrl()));
//         assertThat(updateBasicRespDto.getDeletedCount()).isEqualTo(deletedImageList.size());
//     }

//     @Test
//     public void ???????????????????????????() {
//         // given
//         Long storeId = 1L;

//         // stub
//         // ?????? 1. todayHoursRepository??? findByStoreId() ?????? ??? TodayHours ????????? ?????? (????????? ??????
//         // ????????????)
//         TodayHours todayHours = TodayHours.builder().holidayType(1).businessHours("10:00~22:00")
//                 .breakTime("15:00~17:00").lastOrder("21:30").build();
//         when(todayHoursRepository.findByStoreId(storeId)).thenReturn(todayHours);

//         // ?????? 2. commonService??? setState() ?????? ??? ????????? ??????
//         when(commonService.setState(LocalTime.now().toString().substring(0, 5), todayHours.getHolidayType(),
//                 todayHours.getBusinessHours(),
//                 todayHours.getBreakTime(), todayHours.getLastOrder())).thenReturn("?????????");

//         // when
//         TodayHoursRespDto todayHoursRespDto = storeService1.???????????????????????????(storeId);

//         // then
//         assertThat(todayHoursRespDto.getHolidayType()).isEqualTo(todayHours.getHolidayType());
//         assertThat(todayHoursRespDto.getBusinessHours()).isEqualTo(todayHours.getBusinessHours());
//         assertThat(todayHoursRespDto.getBreakTime()).isEqualTo(todayHours.getBreakTime());
//         assertThat(todayHoursRespDto.getLastOrder()).isEqualTo(todayHours.getLastOrder());
//         System.out.println("????????????: " + todayHoursRespDto.getState());
//     }

//     @Test
//     public void ???????????????????????????() {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;

//         UpdateTodayHoursReqDto dto = new UpdateTodayHoursReqDto(true, "??????", "??????", "??????"); // ???????????? ??????

//         // stub
//         // ?????? 1. storeRepository??? mCheckStoreManager() ?????? ??? 1 ??????
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // ?????? 2. todayHoursRepository??? findByStoreId() ?????? ??? TodayHours ????????? ?????? (????????? ??????
//         // ?????????)
//         TodayHours todayHours = TodayHours.builder().holidayType(1).businessHours("10:00~22:00")
//                 .breakTime("15:00~17:00").lastOrder("21:30").build();
//         when(todayHoursRepository.findByStoreId(storeId)).thenReturn(todayHours);

//         // ?????? 3. holidaysRepository??? mFindHolidaysByStoreId() ?????? ??? ??? ?????? ?????? (???????????? ??????)
//         when(holidaysRepository.mFindHolidaysByStoreId(storeId)).thenReturn("");

//         // ?????? 4. commonService??? setState() ?????? ??? ???????????? ??????
//         when(commonService.setState(LocalTime.now().toString().substring(0, 5), 4, "??????",
//                 "??????", "??????")).thenReturn("????????????");

//         // when
//         TodayHoursRespDto todayHoursRespDto = storeService1.???????????????????????????(storeId, userId, dto);

//         // then
//         // ????????? (1) -> ???????????? ???????????? (4) ?????????
//         assertThat(todayHoursRespDto.getHolidayType()).isEqualTo(4);
//         assertThat(todayHoursRespDto.getBusinessHours()).isEqualTo("??????");
//         assertThat(todayHoursRespDto.getBreakTime()).isEqualTo("??????");
//         assertThat(todayHoursRespDto.getLastOrder()).isEqualTo("??????");
//         System.out.println("????????????: " + todayHoursRespDto.getState());
//     }

//     @Test
//     public void ?????????????????????() {
//         // given
//         Long storeId = 1L;

//         // stub: tablesRepository??? findByStoreId() ?????? ??? Tables ????????? ??????
//         Tables tables = Tables.builder().allTableCount(10).tableCount(10).paused(false)
//                 .modifiedDate(LocalDateTime.now()).build();
//         when(tablesRepository.findByStoreId(storeId)).thenReturn(tables);

//         // when
//         TablesRespDto tablesRespDto = storeService1.?????????????????????(storeId);

//         // then
//         assertThat(tablesRespDto.getAllTableCount()).isEqualTo(tables.getAllTableCount());
//         assertThat(tablesRespDto.getTableCount()).isEqualTo(tables.getTableCount());
//         assertThat(tablesRespDto.isPaused()).isEqualTo(tables.isPaused());
//         assertThat(tablesRespDto.getModifiedDate())
//                 .isEqualTo(tables.getModifiedDate().toString().substring(5, 16).replace("T", " ").replace("-", "."));
//     }

//     @Test
//     public void ?????????????????????() {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;
//         UpdateTablesReqDto dto = new UpdateTablesReqDto(0, false); // ????????? ??? ??????

//         // stub
//         // ?????? 1. storeRepository??? mCheckStoreManager() ?????? ??? 1 ??????
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // ?????? 2. tablesRepository??? findByStoreId() ?????? ??? Tables ????????? ??????
//         int tableCount = 10;
//         Tables tables = Tables.builder().allTableCount(tableCount).tableCount(tableCount).paused(false).build();
//         when(tablesRepository.findByStoreId(storeId)).thenReturn(tables);

//         // when
//         Tables tablesEntity = storeService1.?????????????????????(storeId, userId, dto);

//         // then
//         assertThat(tablesEntity.getAllTableCount()).isEqualTo(tableCount);
//         assertThat(tablesEntity.getTableCount()).isEqualTo(tableCount - 1);
//         assertThat(tablesEntity.isPaused()).isEqualTo(false);
//     }

//     @Test
//     public void ????????????() {
//         // given
//         Long storeId = 1L;
//         Long userId = 1L;

//         // stub
//         // ?????? 1. storeRepository??? mCheckStoreManager() ?????? ??? 1 ??????
//         when(storeRepository.mCheckStoreManager(storeId, userId)).thenReturn(1);

//         // ?????? 2. basicImageRepository??? mFindAllImageUrlByStoreId() ?????? ??? List<String> ??????
//         // ??????
//         List<String> basicImageUrlList = List.of("imageUrl1.png");
//         when(basicImageRepository.mFindAllImageUrlByStoreId(storeId)).thenReturn(basicImageUrlList);

//         // ?????? 3. insideImageRepository??? mFindAllImageUrlByStoreId() ?????? ??? List<String> ??????
//         // ??????
//         List<String> insideImageUrlList = List.of("imageUrl1.png", "imageUrl2.png");
//         when(insideImageRepository.mFindAllImageUrlByStoreId(storeId)).thenReturn(insideImageUrlList);

//         // ?????? 4. menuImageRepository??? mFindAllImageUrlByStoreId() ?????? ??? List<String> ??????
//         // ??????
//         List<String> menuImageUrlList = List.of("imageUrl1.png", "imageUrl2.png", "imageUrl3.png");
//         when(menuImageRepository.mFindAllImageUrlByStoreId(storeId)).thenReturn(menuImageUrlList);

//         // ?????? 5. noticeRepository??? mFindAllIdByStoreId() ?????? ??? List<Long> ?????? ??????
//         List<Long> noticeIdList = List.of(1L, 2L, 3L, 4L);
//         when(noticeRepository.mFindAllIdByStoreId(storeId)).thenReturn(noticeIdList);

//         // ?????? 6. noticeImageRepository??? mFindAllImageUrlByNoticeId() ?????? ??? List<String>
//         // ?????? ??????
//         List<String> noticeImageUrlList = List.of("imageUrl1.png");
//         when(noticeImageRepository.mFindAllImageUrlByNoticeId(anyLong())).thenReturn(noticeImageUrlList);

//         // ?????? 7. storeRepository??? mFindNameByStoreId() ?????? ??? String ?????? ??????
//         String storeName = "?????????";
//         when(storeRepository.mFindNameByStoreId(storeId)).thenReturn(storeName);

//         // when
//         DeleteStoreRespDto deleteStoreRespDto = storeService1.????????????(storeId, userId);

//         // then
//         assertThat(deleteStoreRespDto.getName()).isEqualTo(storeName);
//         assertThat(deleteStoreRespDto.getDeletedBasicImageCount()).isEqualTo(basicImageUrlList.size());
//         assertThat(deleteStoreRespDto.getDeletedInsideImageCount()).isEqualTo(insideImageUrlList.size());
//         assertThat(deleteStoreRespDto.getDeletedMenuImageCount()).isEqualTo(menuImageUrlList.size());
//         assertThat(deleteStoreRespDto.getDeletedNoticeCount()).isEqualTo(noticeIdList.size());
//     }

//     public void ??????????????????_???????????????() {
//         EntityManager em = emf.createEntityManager();
//         EntityTransaction tx = null;
//         try {
//             tx = em.getTransaction();
//             tx.begin();

//             user1 = User.builder().username("tablenow").password("tablenow1!").name("?????????").phone("01011111111")
//                     .uniqueKey("uniqueKey").email("tablenow@naver.com").build();
//             em.persist(user1); // ????????? ??? id ?????? ?????????

//             store1 = Store.builder().name("?????? ?????????").category("??????").phone("02-1111-1111").address("???????????????1")
//                     .jibunAddress("????????? ????????? ?????????").latitude(37.1).longitude(127.1).description("????????????1").website("")
//                     .user(user1).build();
//             store2 = Store.builder().name("?????????2").category("??????").phone("02-2222-2222").address("???????????????2")
//                     .jibunAddress("????????????2").latitude(37.2).longitude(127.2).description("????????????2").website("").user(user1)
//                     .build();
//             em.persist(store1);
//             em.persist(store2);

//             LocalDate today = LocalDate.now();
//             LocalDateTime createdDate = LocalDateTime.now();
//             todayHours1 = TodayHours.builder().holidayType(1).businessHours("10:00~22:00")
//                     .breakTime("15:00~17:00").lastOrder("21:30").today(today.minusDays(1)).store(store1)
//                     .modifiedDate(createdDate).build();
//             todayHours2 = TodayHours.builder().holidayType(2).businessHours("??????")
//                     .breakTime("??????").lastOrder("??????").today(today.minusDays(1)).store(store2).modifiedDate(createdDate)
//                     .build();
//             em.persist(todayHours1);
//             em.persist(todayHours2);

//             basicImage1 = BasicImage.builder().imageUrl("basicImageUrl1.png").store(store1).build();
//             basicImage2 = BasicImage.builder().imageUrl("basicImageUrl2.png").store(store2).build();
//             em.persist(basicImage1);
//             em.persist(basicImage2);

//             tx.commit();
//         } catch (Exception e) {
//             e.printStackTrace();
//             if (tx != null) {
//                 tx.rollback();
//             }
//             throw new RuntimeException(e);
//         } finally {
//             em.close();
//         }
//     }

//     public void ??????????????????_???????????????() {
//         EntityManager em = emf.createEntityManager();
//         EntityTransaction tx = null;
//         try {
//             tx = em.getTransaction();
//             tx.begin();

//             store1 = Store.builder().name("?????? ?????????").category("??????").phone("02-1111-1111").address("???????????????1")
//                     .jibunAddress("????????? ????????? ?????????").latitude(37.1).longitude(127.1).description("????????????1").website("")
//                     .user(user1).build();
//             em.persist(store1);

//             basicImage1 = BasicImage.builder().imageUrl("basicImageUrl1.png").store(store1).build();
//             em.persist(basicImage1);

//             tx.commit();
//         } catch (Exception e) {
//             e.printStackTrace();
//             if (tx != null) {
//                 tx.rollback();
//             }
//             throw new RuntimeException(e);
//         } finally {
//             em.close();
//         }
//     }
// }
