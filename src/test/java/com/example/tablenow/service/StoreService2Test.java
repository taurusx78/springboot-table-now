// package com.example.tablenow.service;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;

// import java.sql.Date;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import javax.persistence.EntityManager;
// import javax.persistence.EntityManagerFactory;
// import javax.persistence.EntityTransaction;
// import javax.persistence.Persistence;
// import javax.persistence.Query;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.qlrm.mapper.JpaResultMapper;
// import org.springframework.test.context.ActiveProfiles;

// import com.example.tablenow.domain.basic_image.BasicImage;
// import com.example.tablenow.domain.inside_image.InsideImage;
// import com.example.tablenow.domain.menu_image.MenuImage;
// import com.example.tablenow.domain.notice.Notice;
// import com.example.tablenow.domain.notice_image.NoticeImage;
// import com.example.tablenow.domain.store.Store;
// import com.example.tablenow.domain.store.StoreRepository;
// import com.example.tablenow.domain.tables.Tables;
// import com.example.tablenow.domain.today_hours.TodayHours;
// import com.example.tablenow.domain.today_hours.TodayHoursRepository;
// import com.example.tablenow.domain.user.User;
// import com.example.tablenow.service.store.StoreCommonService;
// import com.example.tablenow.service.store.StoreService2;
// import com.example.tablenow.web.dto.store.DetailsRespDto;
// import com.example.tablenow.web.dto.store.NameRespDto;
// import com.example.tablenow.web.dto.store.SelectStateRespDto;
// import com.example.tablenow.web.dto.store.SelectStoreRespDto;
// import com.example.tablenow.web.dto.store.SelectUserRespDto;
// import com.example.tablenow.web.dto.store.StateRespDto;
// import com.example.tablenow.web.dto.store.StoreReqDto;
// import com.example.tablenow.web.dto.store.StoreRespDto;

// // Repository를 제외한 Service만 테스트하기 위해 가짜데이터 생성
// // @Mock: 진짜 객체를 추상화된 가짜 객체로 만들어서 Mockito 환경에 주입

// @ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
// @ExtendWith(MockitoExtension.class) // 가짜 메모리 환경 생성
// public class StoreService2Test {

//     @InjectMocks // 가짜데이터 주입 받음
//     private StoreService2 storeService2;

//     @Mock // 가짜 객체 생성
//     private StoreCommonService commonService;

//     @Mock
//     private StoreRepository storeRepository;

//     @Mock
//     private TodayHoursRepository todayHoursRepository;

//     // resources/META-INF/persistence.xml H2 설정 적용 (네이티브쿼리 테스트용)
//     final String PERSISTENCE_UNIT_NAME = "jpa";
//     EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

//     User user1;
//     Store store1;
//     Store store2;
//     BasicImage basicImage1;
//     BasicImage basicImage2;
//     TodayHours todayHours1;
//     TodayHours todayHours2;
//     Tables tables1;
//     Tables tables2;

//     @BeforeEach
//     public void 데이터준비() {
//         EntityManager em = emf.createEntityManager();
//         EntityTransaction tx = null;
//         try {
//             tx = em.getTransaction();
//             tx.begin();

//             user1 = User.builder().username("tablenow").password("tablenow1!").name("김지은").phone("01011111111")
//                     .uniqueKey("uniqueKey").email("tablenow@naver.com").build();
//             em.persist(user1);

//             store1 = Store.builder().name("거궁 동탄점").category("한식").phone("02-1111-1111").address("도로명주소1")
//                     .jibunAddress("경기도 화성시 반송동").latitude(37.1).longitude(127.1).description("매장소개1").website("")
//                     .user(user1).build();
//             store2 = Store.builder().name("매장명2").category("카페").phone("02-2222-2222").address("도로명주소2")
//                     .jibunAddress("지번주소2").latitude(37.2).longitude(127.2).description("매장소개2").website("").user(user1)
//                     .build();
//             em.persist(store1);
//             em.persist(store2);

//             LocalDate today = LocalDate.now();
//             LocalDateTime createdDate = LocalDateTime.now();
//             todayHours1 = TodayHours.builder().holidayType(1).businessHours("10:00~22:00")
//                     .breakTime("15:00~17:00").lastOrder("21:30").today(today.minusDays(1)).store(store1)
//                     .modifiedDate(createdDate).build();
//             todayHours2 = TodayHours.builder().holidayType(2).businessHours("없음")
//                     .breakTime("없음").lastOrder("없음").today(today.minusDays(1)).store(store2).modifiedDate(createdDate)
//                     .build();
//             em.persist(todayHours1);
//             em.persist(todayHours2);

//             tables1 = Tables.builder().allTableCount(10).tableCount(10).paused(false).store(store1)
//                     .modifiedDate(createdDate).build();
//             tables2 = Tables.builder().allTableCount(20).tableCount(20).paused(true).store(store2)
//                     .modifiedDate(createdDate).build();
//             em.persist(tables1);
//             em.persist(tables2);

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

//     @Test
//     public void 매장명전체조회() {
//         // given

//         // stub

//         // when
//         EntityManager em = emf.createEntityManager();
//         String sql = "SELECT id, name FROM store";
//         Query query = em.createNativeQuery(sql);
//         JpaResultMapper result = new JpaResultMapper();
//         List<NameRespDto> dtoList = result.list(query, NameRespDto.class);

//         // then
//         assertThat(dtoList.size()).isEqualTo(2);
//         assertThat(dtoList.get(0).getName()).isEqualTo(store1.getName());
//         assertThat(dtoList.get(1).getName()).isEqualTo(store2.getName());

//         em.close();
//     }

//     @Test
//     public void 즐겨찾기전체조회() {
//         // given
//         List<Long> storeIds = List.of(store2.getId(), store1.getId());

//         // stub

//         // when
//         EntityManager em = emf.createEntityManager();
//         String sql = "SELECT s.id, s.name, s.category, s.jibunAddress, b.imageUrl AS basicImageUrl, t.today, t.holidayType, "
//                 + "t.businessHours, t.breakTime, t.lastOrder, t.modifiedDate AS todayHoursModified, "
//                 + "tb.tableCount, tb.paused, tb.modifiedDate AS tablesModified "
//                 + "FROM store s, basic_image b, today_hours t, tables tb "
//                 + "WHERE b.storeId = s.id AND t.storeId = s.id AND tb.storeId = s.id AND s.id IN (:storeIds) "
//                 // + "GROUP BY s.id ORDER BY FIELD(s.id, :storeIds)";
//                 + "GROUP BY s.id"; // MySQL과 달리 H2는 FIELD를 이용한 우선순위 정렬 불가
//         Query query = em.createNativeQuery(sql).setParameter("storeIds", storeIds);
//         JpaResultMapper result = new JpaResultMapper();
//         List<SelectStoreRespDto> selectDtoList = result.list(query, SelectStoreRespDto.class);

//         // then
//         SelectStoreRespDto selectDto = selectDtoList.get(0);
//         assertThat(selectDtoList.size()).isEqualTo(storeIds.size());
//         assertThat(selectDto.getName()).isEqualTo(store1.getName());
//         assertThat(selectDto.getBasicImageUrl()).isEqualTo(basicImage1.getImageUrl());
//         assertThat(selectDto.getHolidayType()).isEqualTo(todayHours1.getHolidayType());
//         assertThat(selectDto.getTableCount()).isEqualTo(tables1.getTableCount());
//     }

//     @Test
//     public void 검색매장전체조회() {
//         // given
//         StoreReqDto dto = new StoreReqDto("매장명", 37.0, 127.0);

//         // stub

//         // when
//         EntityManager em = emf.createEntityManager();
//         String sql = "SELECT s.id, s.name, s.category, s.jibunAddress, b.imageUrl AS basicImageUrl, t.today, t.holidayType, "
//                 + "t.businessHours, t.breakTime, t.lastOrder, t.modifiedDate AS todayHoursModified, "
//                 + "tb.tableCount, tb.paused, tb.modifiedDate AS tablesModified, "
//                 + "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) "
//                 + "- radians(:longitude)) + sin (radians(:latitude)) * sin(radians(latitude)))) AS distance "
//                 + "FROM store s, basic_image b, today_hours t, tables tb "
//                 + "WHERE b.storeId = s.id AND t.storeId = s.id AND tb.storeId = s.id "
//                 + "AND s.name LIKE CONCAT('%', :name, '%') GROUP BY s.id";
//         Query query = em.createNativeQuery(sql).setParameter("latitude", dto.getLatitude())
//                 .setParameter("longitude", dto.getLongitude()).setParameter("name", dto.getData());
//         JpaResultMapper result = new JpaResultMapper();
//         List<SelectStoreRespDto> selectDtoList = result.list(query, SelectStoreRespDto.class);

//         // then
//         System.out.println("검색 매장수: " + selectDtoList.size());
//         assertThat(selectDtoList.get(0).getName()).contains(dto.getData());
//     }

//     @Test
//     public List<SelectStoreRespDto> 카테고리매장전체조회() {
//         // given
//         StoreReqDto dto = new StoreReqDto("한식", 37.0, 127.0);
//         int length = 500;// 반경 몇 km 까지 조회할 지 설정

//         // stub

//         // when
//         EntityManager em = emf.createEntityManager();
//         String sql = "SELECT s.id, s.name, s.category, s.jibunAddress, b.imageUrl AS basicImageUrl, t.today, t.holidayType, "
//                 + "t.businessHours, t.breakTime, t.lastOrder, t.modifiedDate AS todayHoursModified, "
//                 + "tb.tableCount, tb.paused, tb.modifiedDate AS tablesModified, "
//                 + "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) "
//                 + "- radians(:longitude)) + sin (radians(:latitude)) * sin(radians(latitude)))) AS distance "
//                 + "FROM store s, basic_image b, today_hours t, tables tb "
//                 + "WHERE b.storeId = s.id AND t.storeId = s.id AND tb.storeId = s.id "
//                 + "AND s.category = :category GROUP BY s.id HAVING distance < :length";
//         Query query = em.createNativeQuery(sql).setParameter("latitude", dto.getLatitude())
//                 .setParameter("longitude", dto.getLongitude()).setParameter("category", dto.getData())
//                 .setParameter("length", length);
//         JpaResultMapper result = new JpaResultMapper();
//         List<SelectStoreRespDto> selectDtoList = result.list(query, SelectStoreRespDto.class);

//         // then
//         System.out.println("카테고리 매장수: " + selectDtoList.size());
//         assertThat(selectDtoList.get(0).getCategory()).isEqualTo(dto.getData());

//         return selectDtoList;
//     }

//     @Test
//     public void 매장전체조회() {
//         // given
//         List<SelectStoreRespDto> selectDtoList = 카테고리매장전체조회();

//         // stub
//         // 가설 1. StoreCommonService의 오늘의영업시간수정_날짜변경() 실행 시 TodayHours 타입 리턴
//         TodayHours todayHours = TodayHours.builder().holidayType(1).businessHours("10:00~22:00")
//                 .breakTime("15:00~17:00").lastOrder("21:30").build();
//         when(commonService.오늘의영업시간수정_날짜변경(any(LocalDate.class), any(Long.class))).thenReturn(todayHours);
//         // 가설 2. StoreCommonService의 setState() 실행 시 "영업중" 리턴
//         when(commonService.setState(any(String.class), any(Integer.class), any(String.class), any(String.class),
//                 any(String.class))).thenReturn("영업중");

//         // when
//         List<StoreRespDto> storeRespDtoList = storeService2.매장전체조회(selectDtoList);

//         // then
//         assertThat(storeRespDtoList.get(0).getName()).isEqualTo(selectDtoList.get(0).getName());
//         assertThat(storeRespDtoList.get(0).getState()).isEqualTo("영업중");
//     }

//     @Test
//     public void 매장상세조회() throws Exception {
//         // given
//         Long storeId = store1.getId();

//         // stub
//         // 가설 1. storeRepository의 findById() 실행 시 Optional Store 리턴
//         LocalDateTime createdDate = LocalDateTime.now();
//         InsideImage insideImage1 = InsideImage.builder().imageUrl("insideImageUrl1.png").store(store1).createdDate(createdDate)
//                 .build();
//         MenuImage menuImage1 = MenuImage.builder().imageUrl("menuImageUrl1.png").store(store1).createdDate(createdDate)
//                 .build();
//         NoticeImage noticeImage = NoticeImage.builder().imageUrl("noticeImageUrl.png").build();
//         Notice notice1 = Notice.builder().title("알림 제목").title("알림 내용").holidayStartDate("").holidayEndDate("")
//                 .noticeImageList(List.of(noticeImage)).store(store1).createdDate(createdDate).build();
//         store1.setForTest(List.of(basicImage1), List.of(insideImage1), List.of(menuImage1), todayHours1, tables1,
//                 List.of(notice1));
//         when(storeRepository.findById(storeId)).thenReturn(Optional.of(store1));

//         // 가설 2. StoreCommonService의 오늘의영업시간수정_날짜변경() 실행 시 TodayHours 타입 리턴
//         TodayHours todayHours = TodayHours.builder().holidayType(1).businessHours("10:00~22:00")
//                 .breakTime("15:00~17:00").lastOrder("21:30").build();
//         when(commonService.오늘의영업시간수정_날짜변경(any(LocalDate.class), any(Long.class))).thenReturn(todayHours);

//         // 가설 3. StoreCommonService의 setState() 실행 시 "영업중" 리턴
//         when(commonService.setState(any(String.class), any(Integer.class), any(String.class), any(String.class),
//                 any(String.class))).thenReturn("영업중");

//         // when
//         DetailsRespDto detailsRespDto = storeService2.매장상세조회(storeId);

//         // then
//         assertThat(detailsRespDto.getName()).isEqualTo(store1.getName()); // Store 테스트
//         assertTrue(detailsRespDto.getBasicImageUrlList().contains(basicImage1.getImageUrl())); // BasicImage 테스트
//         assertTrue(detailsRespDto.getInsideImageUrlList().contains(insideImage1.getImageUrl())); // InsideImage 테스트
//         assertTrue(detailsRespDto.getMenuImageUrlList().contains(menuImage1.getImageUrl())); // MenuImage 테스트
//         assertThat(detailsRespDto.getTableCount()).isEqualTo(tables1.getTableCount()); // Tables 테스트
//         assertThat(detailsRespDto.getBusinessHours()).isEqualTo(todayHours1.getBusinessHours()); // TodayHours 테스트
//         assertThat(detailsRespDto.getState()).isEqualTo("영업중"); // 영업상태 테스트
//         assertThat(detailsRespDto.getNoticeList().get(0).getTitle()).isEqualTo(notice1.getTitle()); // Notice 테스트
//     }

//     @Test
//     public SelectStateRespDto 영업상태조회() {
//         // given
//         Long storeId = store1.getId();

//         // stub

//         // when
//         EntityManager em = emf.createEntityManager();
//         String sql = "SELECT t.holidayType, t.businessHours, t.breakTime, t.lastOrder, t.modifiedDate AS todayModified, "
//                 + "tb.tableCount, tb.paused, tb.modifiedDate AS tablesModified "
//                 + "FROM today_hours t INNER JOIN tables tb "
//                 + "ON t.storeId = tb.storeId WHERE t.storeId = :storeId";
//         Query query = em.createNativeQuery(sql).setParameter("storeId", storeId);
//         JpaResultMapper result = new JpaResultMapper();
//         SelectStateRespDto selectDto = result.uniqueResult(query, SelectStateRespDto.class);

//         // then
//         assertThat(selectDto.getHolidayType()).isEqualTo(todayHours1.getHolidayType());
//         assertThat(selectDto.getTableCount()).isEqualTo(tables1.getTableCount());

//         return selectDto;
//     }

//     @Test
//     void 영업상태업데이트() {
//         // given
//         Long storeId = store1.getId();
//         SelectStateRespDto selectDto = 영업상태조회();

//         // stub
//         // 가설 1. todayHoursRepository의 mFindTodayByStoreId() 실행 시 Date 타입 (어제 날짜) 리턴
//         when(todayHoursRepository.mFindTodayByStoreId(storeId))
//                 .thenReturn(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));

//         // 가설 2. StoreCommonService의 오늘의영업시간수정_날짜변경() 실행 시 TodayHours 타입 리턴
//         TodayHours todayHours = TodayHours.builder().holidayType(1).businessHours("10:00~22:00")
//                 .breakTime("15:00~17:00").lastOrder("21:30").build();
//         when(commonService.오늘의영업시간수정_날짜변경(any(LocalDate.class), any(Long.class))).thenReturn(todayHours);

//         // 가설 3. StoreCommonService의 setState() 실행 시 "영업중" 리턴
//         when(commonService.setState(any(String.class), any(Integer.class), any(String.class), any(String.class),
//                 any(String.class))).thenReturn("영업중");

//         // when
//         StateRespDto stateRespDto = storeService2.영업상태업데이트(storeId, selectDto);

//         // then
//         assertThat(stateRespDto.getState()).isEqualTo("영업중");
//         assertThat(stateRespDto.getTableCount()).isEqualTo(selectDto.getTableCount());
//         assertThat(stateRespDto.getBusinessHours()).isEqualTo(selectDto.getBusinessHours());
//     }

//     @Test
//     public void 매장수정제안() {
//         // given
//         Long storeId = store1.getId();

//         // stub

//         // when
//         EntityManager em = emf.createEntityManager();
//         String sql = "SELECT s.name AS storeName, u.name AS managerName, u.email "
//                 + "FROM store s INNER JOIN users u "
//                 + "ON s.userId = u.id WHERE s.id = :storeId";
//         Query query = em.createNativeQuery(sql).setParameter("storeId", storeId);
//         JpaResultMapper result = new JpaResultMapper();
//         SelectUserRespDto selectDto = result.uniqueResult(query, SelectUserRespDto.class);

//         // then
//         assertThat(selectDto.getStoreName()).isEqualTo(store1.getName());
//         assertThat(selectDto.getManagerName()).isEqualTo(user1.getName());
//         assertThat(selectDto.getEmail()).isEqualTo(user1.getEmail());
//     }
// }
