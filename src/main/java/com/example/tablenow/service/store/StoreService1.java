package com.example.tablenow.service.store;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.qlrm.mapper.JpaResultMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tablenow.domain.basic_image.BasicImage;
import com.example.tablenow.domain.basic_image.BasicImageRepository;
import com.example.tablenow.domain.holidays.Holidays;
import com.example.tablenow.domain.holidays.HolidaysRepository;
import com.example.tablenow.domain.hours.Hours;
import com.example.tablenow.domain.hours.HoursRepository;
import com.example.tablenow.domain.image_modified_date.ImageModifiedDateRepository;
import com.example.tablenow.domain.inside_image.InsideImage;
import com.example.tablenow.domain.inside_image.InsideImageRepository;
import com.example.tablenow.domain.menu_image.MenuImage;
import com.example.tablenow.domain.menu_image.MenuImageRepository;
import com.example.tablenow.domain.notice.Notice;
import com.example.tablenow.domain.notice.NoticeRepository;
import com.example.tablenow.domain.notice_image.NoticeImageRepository;
import com.example.tablenow.domain.store.Store;
import com.example.tablenow.domain.store.StoreRepository;
import com.example.tablenow.domain.tables.Tables;
import com.example.tablenow.domain.tables.TablesRepository;
import com.example.tablenow.domain.today_hours.TodayHours;
import com.example.tablenow.domain.today_hours.TodayHoursRepository;
import com.example.tablenow.domain.user.User;
import com.example.tablenow.handler.exception.CustomException;
import com.example.tablenow.web.dto.store.StoreReqDto.CheckExistReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.SaveStoreReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateBasicReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateHoursReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateInsideReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateMenuReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateTablesReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateTodayHoursReqDto;
import com.example.tablenow.web.dto.store.StoreRespDto.BasicRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.DeleteStoreRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.HolidaysRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.HoursRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.InsideRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.MenuRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.MyStoreRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.OneMyStoreRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.SaveStoreRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.StoreListRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.TablesRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.TodayHoursRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.UpdateBasicRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.UpdateHolidaysRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.UpdateHoursRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.UpdateInsideRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.UpdateMenuRespDto;

import lombok.RequiredArgsConstructor;

// 매장용 StoreService

@RequiredArgsConstructor
@Service
public class StoreService1 {

    private final StoreRepository storeRepository;
    private final BasicImageRepository basicImageRepository;
    private final InsideImageRepository insideImageRepository;
    private final MenuImageRepository menuImageRepository;
    private final ImageModifiedDateRepository imageModifiedDateRepository;
    private final HolidaysRepository holidaysRepository;
    private final HoursRepository hoursRepository;
    private final TodayHoursRepository todayHoursRepository;
    private final TablesRepository tablesRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;

    private final StoreCommonService commonService;
    private final EntityManager em; // 네이티브 쿼리를 작성하기 위해 필요함

    @Transactional(readOnly = true)
    public int 등록여부조회(CheckExistReqDto dto) {
        return storeRepository.mCheckExist(dto.getBusinessRegNum());
    }

    @Transactional
    public SaveStoreRespDto 매장등록(User user, SaveStoreReqDto dto) {
        // 1. Store 테이블에 INSERT
        Store storeEntity = storeRepository.save(dto.toStoreEntity(user));

        List<BasicImage> basicImageList = new ArrayList<>(); // 테이블 INSERT용
        List<String> basicImageUrlList = new ArrayList<>(); // 서버 저장용
        for (MultipartFile file : dto.getBasicImageFileList()) {
            String imageUrl = commonService.makeImageUrl(file);
            basicImageList.add(dto.toBasicImageEntity(imageUrl, storeEntity));
            basicImageUrlList.add(imageUrl);
        }
        // 2. BasicImage 테이블에 INSERT
        basicImageRepository.saveAll(basicImageList);

        List<InsideImage> insideImageList = new ArrayList<>();
        List<String> insideImageUrlList = new ArrayList<>();
        for (MultipartFile file : dto.getInsideImageFileList()) {
            String imageUrl = commonService.makeImageUrl(file);
            insideImageList.add(dto.toInsideImageEntity(imageUrl, storeEntity));
            insideImageUrlList.add(imageUrl);
        }
        // 3. InsideImage 테이블에 INSERT
        insideImageRepository.saveAll(insideImageList);

        List<MenuImage> menuImageList = new ArrayList<>();
        List<String> menuImageUrlList = new ArrayList<>();
        for (MultipartFile file : dto.getMenuImageFileList()) {
            String imageUrl = commonService.makeImageUrl(file);
            menuImageList.add(dto.toMenuImageEntity(imageUrl, storeEntity));
            menuImageUrlList.add(imageUrl);
        }
        // 4. MenuImage 테이블에 INSERT
        menuImageRepository.saveAll(menuImageList);

        // ImageModifiedDate 테이블에 INSERT
        imageModifiedDateRepository.save(dto.toImageModifiedDate(storeEntity));

        // 5. Holidays 테이블에 INSERT
        Holidays holidaysEntity = holidaysRepository.save(dto.toHolidaysEntity(storeEntity));

        // 6. Hours 테이블에 INSERT
        List<Hours> hoursEntityList = hoursRepository.saveAll(dto.toHoursEntityList(storeEntity));

        // 7. Tables 테이블에 INSERT
        Tables tablesEntity = tablesRepository.save(dto.toTablesEntity(storeEntity));

        // 8. TodayHours 테이블에 INSERT
        TodayHours todayHoursEntity = todayHoursRepository.save(dto.toTodayHoursEntity(storeEntity));
        todayHoursEntity = commonService.오늘의영업시간수정_날짜변경(LocalDate.now(), storeEntity.getId());

        // 서버에 대표사진 저장
        commonService.saveImagesToServer("basic", basicImageUrlList, dto.getBasicImageFileList());
        // 서버에 매장내부사진 저장
        commonService.saveImagesToServer("inside", insideImageUrlList, dto.getInsideImageFileList());
        // 서버에 메뉴사진 저장
        commonService.saveImagesToServer("menu", menuImageUrlList, dto.getMenuImageFileList());

        return new SaveStoreRespDto(storeEntity, basicImageUrlList, insideImageUrlList, menuImageUrlList,
                holidaysEntity.getHolidays(), hoursEntityList, tablesEntity.getAllTableCount(),
                todayHoursEntity.getToday().toString());
    }

    // (주의) TodayHours 테이블 UPDATE 가능성 있기 때문에 readOnly=true 설정하면 안됨!
    @Transactional
    public StoreListRespDto<MyStoreRespDto> 나의매장전체조회(Long userId) {
        // SELECT하는 데이터 타입이 Store가 아닌 MyStoreResp이기 때문에
        // StoreRepository 사용 못하고 직접 쿼리를 작성해 주어야 함
        // 1. 쿼리 준비
        String sql = "SELECT s.id, s.name, s.address, s.detailAddress, s.phone, b.imageUrl AS basicImageUrl, "
                + "t.today, t.holidayType, t.businessHours, t.breakTime, t.lastOrder "
                + "FROM store s, basic_image b, today_hours t "
                + "WHERE s.userId = :userId AND b.storeId = s.id AND t.storeId = s.id GROUP BY s.id";
        // 2. 쿼리 완성
        Query query = em.createNativeQuery(sql).setParameter("userId", userId);
        // 3. 쿼리 실행 (qlrm 라이브러리 필요 -> DTO에 쿼리 결과 매핑해줌)
        JpaResultMapper result = new JpaResultMapper();
        List<MyStoreRespDto> myStoreList = result.list(query, MyStoreRespDto.class);

        LocalDate today = LocalDate.now(); // 오늘 날짜
        String now = LocalTime.now().toString().substring(0, 5); // 현재 시간

        for (MyStoreRespDto myStore : myStoreList) {
            // 오늘의 영업시간 업데이트 여부 확인
            if (myStore.getToday().toLocalDate().isBefore(today)) {
                // TodayHours 테이블 UPDATE
                TodayHours todayHours = commonService.오늘의영업시간수정_날짜변경(today, myStore.getId().longValue());
                myStore.updateTodayHours(Date.valueOf(today), todayHours.getHolidayType(),
                        todayHours.getBusinessHours(), todayHours.getBreakTime(), todayHours.getLastOrder());
            }

            // 영업상태 설정
            String state = commonService.setState(myStore.getId(), now, myStore.getHolidayType(),
                    myStore.getBusinessHours(),
                    myStore.getBreakTime(), myStore.getLastOrder());
            myStore.setState(state);
        }
        return new StoreListRespDto<>(myStoreList);
    }

    // (주의) TodayHours 테이블 UPDATE 가능성 있기 때문에 readOnly=true 설정하면 안됨!
    @Transactional
    public OneMyStoreRespDto 나의매장1개조회(Long storeId) {
        // SELECT하는 데이터 타입이 Store이 아닌 MyStoreResp이기 때문에
        // StoreRepository 사용 못하고 직접 쿼리를 작성해 주어야 함
        String sql = "SELECT s.id, s.name, b.imageUrl AS basicImageUrl, "
                + "t.today, t.holidayType, t.businessHours, t.breakTime, t.lastOrder, "
                + "tb.allTableCount, tb.tableCount, tb.paused, tb.modifiedDate "
                + "FROM store s, basic_image b, today_hours t, tables tb "
                + "WHERE s.id = :storeId AND b.storeId = s.id AND t.storeId = s.id GROUP BY s.id";
        Query query = em.createNativeQuery(sql).setParameter("storeId", storeId);
        JpaResultMapper result = new JpaResultMapper();
        OneMyStoreRespDto myStore = result.uniqueResult(query, OneMyStoreRespDto.class);

        LocalDate today = LocalDate.now(); // 오늘 날짜
        String now = LocalTime.now().toString().substring(0, 5); // 현재 시간

        // 오늘의 영업시간 업데이트 여부 확인
        if (myStore.getToday().toLocalDate().isBefore(today)) {
            // TodayHours 테이블 UPDATE
            TodayHours todayHours = commonService.오늘의영업시간수정_날짜변경(today, myStore.getId().longValue());
            myStore.updateTodayHours(Date.valueOf(today), todayHours.getHolidayType(),
                    todayHours.getBusinessHours(), todayHours.getBreakTime(), todayHours.getLastOrder());
        }

        // 영업상태 설정
        String state = commonService.setState(storeId, now, myStore.getHolidayType(), myStore.getBusinessHours(),
                myStore.getBreakTime(), myStore.getLastOrder());
        myStore.setState(state);
        return myStore;
    }

    @Transactional(readOnly = true)
    public HoursRespDto 영업시간조회(Long storeId) {
        List<Hours> hoursEntityList = hoursRepository.findAllByStoreId(storeId);

        String[] openTimeList = { "10:00", "10:00", "10:00", "10:00", "10:00", "10:00", "10:00" };
        String[] closeTimeList = { "22:00", "22:00", "22:00", "22:00", "22:00", "22:00", "22:00" };
        boolean[] run24HoursList = { false, false, false, false, false, false, false };
        boolean[] hasBreakTimeList = { false, false, false, false, false, false, false };
        String[] startBreakTimeList = { "15:00", "15:00", "15:00", "15:00", "15:00", "15:00", "15:00" };
        String[] endBreakTimeList = { "17:00", "17:00", "17:00", "17:00", "17:00", "17:00", "17:00" };
        boolean[] hasLastOrderList = { false, false, false, false, false, false, false };
        String[] lastOrderList = { "22:00", "22:00", "22:00", "22:00", "22:00", "22:00", "22:00" };
        LocalDateTime modifiedDate = hoursEntityList.get(0).getModifiedDate();

        for (int i = 0; i < 7; i++) {
            Hours hours = hoursEntityList.get(i);

            // 1. 영업시간 설정
            String businessHours = hours.getBusinessHours();
            String[] businessHoursPart = businessHours.split(" - ");
            openTimeList[i] = businessHoursPart[0];
            closeTimeList[i] = businessHoursPart[1];
            run24HoursList[i] = businessHoursPart[0].equals(businessHoursPart[1]);

            // 2. 휴게시간 설정
            String breakTime = hours.getBreakTime();
            if (!breakTime.equals("없음")) {
                hasBreakTimeList[i] = true;
                String[] breakTimePart = breakTime.split(" - ");
                startBreakTimeList[i] = breakTimePart[0];
                endBreakTimeList[i] = breakTimePart[1];
            }

            // 3. 주문마감시간 설정
            String lastOrder = hours.getLastOrder();
            if (!lastOrder.equals("없음")) {
                hasLastOrderList[i] = true;
                lastOrderList[i] = lastOrder;
            }
        }

        return HoursRespDto.builder().storeId(storeId).openTimeList(openTimeList).closeTimeList(closeTimeList)
                .run24HoursList(run24HoursList).hasBreakTimeList(hasBreakTimeList)
                .startBreakTimeList(startBreakTimeList).endBreakTimeList(endBreakTimeList)
                .hasLastOrderList(hasLastOrderList).lastOrderList(lastOrderList).modifiedDate(modifiedDate.toString())
                .build();
    }

    @Transactional
    public UpdateHoursRespDto 영업시간수정(Long storeId, Long userId, UpdateHoursReqDto dto) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        List<Hours> hoursEntityList = hoursRepository.findAllByStoreId(storeId);
        for (int i = 0; i < 7; i++) {
            Hours hours = hoursEntityList.get(i);
            hours.update(dto.getBusinessHoursList().get(i), dto.getBreakTimeList().get(i),
                    dto.getLastOrderList().get(i));
        }

        // 오늘 영업일인 경우, TodayHours 테이블 UPDATE
        TodayHours todayHoursEntity = todayHoursRepository.findByStoreId(storeId);
        if (todayHoursEntity.getHolidayType() == 1) {
            int dayOfWeek = LocalDate.now().getDayOfWeek().getValue() - 1;
            todayHoursEntity.update(1, hoursEntityList.get(dayOfWeek).getBusinessHours(),
                    hoursEntityList.get(dayOfWeek).getBreakTime(), hoursEntityList.get(dayOfWeek).getLastOrder());
        }

        // 영업상태 설정
        String state = commonService.setState(storeId, LocalTime.now().toString().substring(0, 5),
                todayHoursEntity.getHolidayType(), todayHoursEntity.getBusinessHours(),
                todayHoursEntity.getBreakTime(), todayHoursEntity.getLastOrder());

        // 변경된 행만 UPDATE 실행됨
        return new UpdateHoursRespDto(hoursEntityList, todayHoursEntity, state);
    }

    @Transactional(readOnly = true)
    public HolidaysRespDto 정기휴무조회(Long storeId) {
        Holidays holidaysEntity = holidaysRepository.findByStoreId(storeId);
        return new HolidaysRespDto(storeId, holidaysEntity);
    }

    @Transactional
    public UpdateHolidaysRespDto 정기휴무수정(Long storeId, Long userId, String holidays) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        // 1. Holidays 데이터 영속화
        Holidays holidaysEntity = holidaysRepository.findByStoreId(storeId);

        // 입력받은 holidays 오름차순 정렬
        String[] splitedHolidays = holidays.split(" ");
        Arrays.sort(splitedHolidays);
        String sortedHolidays = "";
        for (String holiday : splitedHolidays) {
            sortedHolidays += holiday + " ";
        }
        holidays = sortedHolidays.trim();

        // 2. 정기휴무가 변경된 경우, TodayHours 테이블 UPDATE
        TodayHours todayHoursEntity = todayHoursRepository.findByStoreId(storeId);
        if (!holidaysEntity.getHolidays().equals(holidays)) {
            LocalDate today = LocalDate.now();
            int weekOfMonth = today.get(WeekFields.of(Locale.getDefault()).weekOfMonth()); // 오늘 주차
            int dayOfWeek = today.getDayOfWeek().getValue() - 1; // 오늘 요일
            // 오늘 정기휴무 여부
            boolean isHoliday = holidays.contains(String.valueOf(weekOfMonth) + String.valueOf(dayOfWeek));

            // TodayHours 테이블 UPDATE
            if (todayHoursEntity.getHolidayType() != 2 && isHoliday) {
                // 2-1. 정기휴무 X -> O
                todayHoursEntity.update(2, "없음", "없음", "없음");
            } else if (todayHoursEntity.getHolidayType() == 2 && !isHoliday) {
                // 2-2. 정기휴무 O -> X
                // 임시휴무인 경우
                List<Notice> noticeList = noticeRepository.findAllByStoreId(storeId);
                for (Notice notice : noticeList) {
                    if (notice.getHolidayStartDate().compareTo(today.toString()) <= 0
                            && notice.getHolidayEndDate().compareTo(today.toString()) >= 0) {
                        todayHoursEntity.update(3, "없음", "없음", "없음");
                        return new UpdateHolidaysRespDto(holidays, todayHoursEntity, "임시휴무");
                    }
                }
                // 영업일인 경우
                Hours hoursEntityList = hoursRepository.findByDayTypeAndStoreId(dayOfWeek, storeId);
                todayHoursEntity.update(1, hoursEntityList.getBusinessHours(), hoursEntityList.getBreakTime(),
                        hoursEntityList.getLastOrder());
            }
        }

        // 3. Holidays 데이터 수정
        holidaysEntity.update(holidays);

        // 4. 영업상태 설정
        String state = commonService.setState(storeId, LocalTime.now().toString().substring(0, 5),
                todayHoursEntity.getHolidayType(), todayHoursEntity.getBusinessHours(),
                todayHoursEntity.getBreakTime(), todayHoursEntity.getLastOrder());

        return new UpdateHolidaysRespDto(holidays, todayHoursEntity, state);
    }

    @Transactional(readOnly = true)
    public MenuRespDto 메뉴조회(Long storeId) {
        // 1. 메뉴사진 전체조회
        List<MenuImage> menuImageList = menuImageRepository.findAllByStoreId(storeId);
        // 2. 메뉴사진 최종수정일 조회
        Date modifiedDate = imageModifiedDateRepository.mFindMenuModifiedByStoreId(storeId);
        return new MenuRespDto(storeId, menuImageList, modifiedDate);
    }

    @Transactional
    public UpdateMenuRespDto 메뉴수정(Long storeId, Long userId, UpdateMenuReqDto dto) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        int imageCount = menuImageRepository.mCountAllByStoreId(storeId); // 기존 사진 개수
        int deletedCount = dto.getDeletedImageUrlList() == null ? 0 : dto.getDeletedImageUrlList().size(); // 삭제된 사진 개수
        int addedCount = dto.getAddedImageFileList() == null ? 0 : dto.getAddedImageFileList().size(); // 추가된 사진 개수

        // 메뉴사진이 최소 1장 첨부되었는지 확인
        if (imageCount == deletedCount && addedCount == 0) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").response("메뉴사진을 최소 1장 올려주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        }

        // 최대 개수 초과 여부 확인 (기존 - 삭제 + 추가 > 최대)
        if (imageCount - deletedCount + addedCount > 20) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").response("메뉴사진을 20장 이하로 올려주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        }

        // 1. 추가된 메뉴사진이 있는 경우
        List<MenuImage> menuImageList = new ArrayList<>();
        List<String> menuImageUrlList = new ArrayList<>();
        if (addedCount > 0) {
            // Store는 id만 필요하기 때문에, 전체 데이터를 SELECT 할 필요 없음
            Store store = Store.builder().id(storeId).build();
            for (MultipartFile file : dto.getAddedImageFileList()) {
                String imageUrl = commonService.makeImageUrl(file);
                menuImageList.add(dto.toMenuImageEntity(imageUrl, store));
                menuImageUrlList.add(imageUrl);
            }
            // MenuImage 테이블에 INSERT
            menuImageRepository.saveAll(menuImageList);
        }

        // 2. 삭제된 메뉴사진이 있는 경우
        if (deletedCount > 0) {
            for (String imageUrl : dto.getDeletedImageUrlList()) {
                // MenuImage 테이블에서 DELETE
                menuImageRepository.deleteByImageUrl(imageUrl);
            }
        }

        // 3. 메뉴사진 최종수정일 업데이트
        imageModifiedDateRepository.mUpdateMenuModified(storeId, LocalDate.now());

        // 추가된 메뉴사진 서버에 저장
        if (addedCount > 0) {
            commonService.saveImagesToServer("menu", menuImageUrlList, dto.getAddedImageFileList());
        }

        // 삭제된 메뉴사진 서버에서 삭제
        if (deletedCount > 0) {
            commonService.deleteImagesOnServer("menu", dto.getDeletedImageUrlList());
            deletedCount = dto.getDeletedImageUrlList().size();
        }

        return new UpdateMenuRespDto(menuImageList, deletedCount);
    }

    @Transactional(readOnly = true)
    public InsideRespDto 매장내부정보조회(Long storeId) {
        // 1. 전체테이블 수 조회
        int allTableCount = tablesRepository.mFindAllTableCountByStoreId(storeId);
        // 2. 매장내부사진 전체조회
        List<InsideImage> insideImageList = insideImageRepository.findAllByStoreId(storeId);
        // 3. 매장내부사진 최종수정일 조회
        Date modifiedDate = imageModifiedDateRepository.mFindInsideModifiedByStoreId(storeId);
        return new InsideRespDto(storeId, allTableCount, insideImageList, modifiedDate);
    }

    @Transactional
    public UpdateInsideRespDto 매장내부정보수정(Long storeId, Long userId, UpdateInsideReqDto dto) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        int imageCount = insideImageRepository.mCountAllByStoreId(storeId); // 기존 사진 개수
        int deletedCount = dto.getDeletedImageUrlList() == null ? 0 : dto.getDeletedImageUrlList().size(); // 삭제된 사진 개수
        int addedCount = dto.getAddedImageFileList() == null ? 0 : dto.getAddedImageFileList().size(); // 추가된 사진 개수

        // 매장내부사진이 최소 1장 첨부되었는지 확인
        if (imageCount == deletedCount && addedCount == 0) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").response("매장내부사진을 최소 1장 올려주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        }

        // 최대 개수 초과 여부 확인 (기존 - 삭제 + 추가 > 최대)
        if (imageCount - deletedCount + addedCount > 10) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").response("매장내부사진을 10장 이하로 올려주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        }

        // 1. Tables 데이터 영속화
        Tables tablesEntity = tablesRepository.findByStoreId(storeId);

        // 2. Tables 데이터 수정
        tablesEntity.updateAllTableCount(dto.getAllTableCount(), dto.getAllTableCount());

        // 3. 추가된 매장내부사진이 있는 경우
        List<InsideImage> insideImageList = new ArrayList<>();
        List<String> insideImageUrlList = new ArrayList<>();
        if (addedCount > 0) {
            // Store는 id만 필요하기 때문에, 전체 데이터를 SELECT 할 필요 없음
            Store store = Store.builder().id(storeId).build();
            for (MultipartFile file : dto.getAddedImageFileList()) {
                String imageUrl = commonService.makeImageUrl(file);
                insideImageList.add(dto.toInsideImageEntity(imageUrl, store));
                insideImageUrlList.add(imageUrl);
            }
            // InsideImage 테이블에 INSERT
            insideImageRepository.saveAll(insideImageList);
        }

        // 4. 삭제된 매장내부사진이 있는 경우
        if (deletedCount > 0) {
            for (String imageUrl : dto.getDeletedImageUrlList()) {
                // InsideImage 테이블에서 DELETE
                insideImageRepository.deleteByImageUrl(imageUrl);
            }
        }

        // 5. 매장내부사진 최종수정일 업데이트
        imageModifiedDateRepository.mUpdateInsideModified(storeId, LocalDate.now());

        // 추가된 매장내부사진 서버에 저장
        if (addedCount > 0) {
            commonService.saveImagesToServer("inside", insideImageUrlList, dto.getAddedImageFileList());
        }

        // 삭제된 매장내부사진 서버에서 삭제
        if (deletedCount > 0) {
            commonService.deleteImagesOnServer("inside", dto.getDeletedImageUrlList());
            deletedCount = dto.getDeletedImageUrlList().size();
        }

        return new UpdateInsideRespDto(tablesEntity, insideImageList, deletedCount);
    }

    @Transactional(readOnly = true)
    public BasicRespDto 기본정보조회(Long storeId) {
        String sql = "SELECT id, name, category, phone, address, detailAddress, jibunAddress, latitude, longitude, description, website, modifiedDate "
                + "FROM store WHERE id = ?";
        Query query = em.createNativeQuery(sql).setParameter(1, storeId);
        JpaResultMapper result = new JpaResultMapper();
        BasicRespDto dto = result.uniqueResult(query, BasicRespDto.class);

        // 대표사진 최종수정일 조회
        Date modifiedDate = imageModifiedDateRepository.mFindBasicModifiedByStoreId(storeId);
        // 대표사진 리스트 추가 및 최종수정일
        dto.setImageInfo(basicImageRepository.findAllByStoreId(storeId), modifiedDate);
        return dto;
    }

    @Transactional
    public UpdateBasicRespDto 기본정보수정(Long storeId, Long userId, UpdateBasicReqDto dto) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        int imageCount = basicImageRepository.mCountAllByStoreId(storeId); // 기존 사진 개수
        int deletedCount = dto.getDeletedImageUrlList() == null ? 0 : dto.getDeletedImageUrlList().size(); // 삭제된 사진 개수
        int addedCount = dto.getAddedImageFileList() == null ? 0 : dto.getAddedImageFileList().size(); // 추가된 사진 개수

        // 대표사진이 최소 1장 첨부되었는지 확인
        if (imageCount == deletedCount && addedCount == 0) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").response("대표사진을 최소 1장 올려주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        }

        // 최대 개수 초과 여부 확인 (기존 - 삭제 + 추가 > 최대)
        if (imageCount - deletedCount + addedCount > 3) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").response("대표사진을 3장 이하로 올려주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        }

        // 1. Store 데이터 영속화
        Store storeEntity = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NoResultException("존재하지 않는 매장");
        });

        // 2 Store 데이터 변경
        storeEntity.update(dto);

        // 3. 추가된 대표사진이 있는 경우
        List<BasicImage> basicImageList = new ArrayList<>();
        List<String> basicImageUrlList = new ArrayList<>();
        if (addedCount > 0) {
            for (MultipartFile file : dto.getAddedImageFileList()) {
                String imageUrl = commonService.makeImageUrl(file);
                basicImageList.add(dto.toBasicImageEntity(imageUrl, storeEntity));
                basicImageUrlList.add(imageUrl);
            }
            // BasicImage 테이블에 INSERT
            basicImageRepository.saveAll(basicImageList);
        }

        // 4. 삭제된 대표사진이 있는 경우
        if (deletedCount > 0) {
            for (String imageUrl : dto.getDeletedImageUrlList()) {
                // BasicImage 테이블에서 DELETE
                basicImageRepository.deleteByImageUrl(imageUrl);
            }
        }

        // 5. 대표사진 최종수정일 업데이트
        imageModifiedDateRepository.mUpdateBasicModified(storeId, LocalDate.now());

        // 추가된 대표사진 서버에 저장
        if (addedCount > 0) {
            commonService.saveImagesToServer("basic", basicImageUrlList, dto.getAddedImageFileList());
        }

        // 삭제된 대표사진 서버에서 삭제
        if (deletedCount > 0) {
            commonService.deleteImagesOnServer("basic", dto.getDeletedImageUrlList());
            deletedCount = dto.getDeletedImageUrlList().size();
        }

        // 5. 대표사진 1개 조회
        String basicImageUrl = basicImageRepository.mFindOneImageUrlByStoreId(storeId);

        return new UpdateBasicRespDto(storeEntity, basicImageUrl, basicImageList, deletedCount);
    }

    @Transactional(readOnly = true)
    public TodayHoursRespDto 오늘의영업시간조회(Long storeId) {
        TodayHours todayHoursEntity = todayHoursRepository.findByStoreId(storeId);
        // 영업상태 설정
        String state = commonService.setState(storeId, LocalTime.now().toString().substring(0, 5),
                todayHoursEntity.getHolidayType(), todayHoursEntity.getBusinessHours(),
                todayHoursEntity.getBreakTime(), todayHoursEntity.getLastOrder());
        return new TodayHoursRespDto(todayHoursEntity, state);
    }

    // 리턴값: 권한 없음 (-1), 수정 불가 (-2), 수정 성공 (1)
    @Transactional
    public TodayHoursRespDto 오늘의영업시간수정(Long storeId, Long userId, UpdateTodayHoursReqDto dto) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        // TodayHours 데이터 영속화
        TodayHours todayHoursEntity = todayHoursRepository.findByStoreId(storeId);

        if (todayHoursEntity.getHolidayType() == 1 && !dto.isHoliday()) {
            // 1. 영업일 (1) -> 영업일 (1) (시간 변경)
            System.out.println("1. 영업일 (1) -> 영업일 (1) (시간 변경)");
            todayHoursEntity.updateInPerson(1, dto.getBusinessHours(), dto.getBreakTime(), dto.getLastOrder());
        } else if (todayHoursEntity.getHolidayType() == 3 && !dto.isHoliday()) {
            System.out.println("2. 알림등록 임시휴무 (3) -> 영업일 (1) (불가능, 알림 삭제/수정으로 가능)");
            // 2. 알림등록 임시휴무 (3) -> 영업일 (1) (불가능, 알림 삭제/수정으로 가능)
        } else if (todayHoursEntity.getHolidayType() == 4 && !dto.isHoliday()) {
            System.out.println("3. 임의변경 임시휴무 (4) -> 영업일 (1)");
            // 3. 임의변경 임시휴무 (4) -> 영업일 (1)
            todayHoursEntity.updateInPerson(1, dto.getBusinessHours(), dto.getBreakTime(), dto.getLastOrder());
        } else if (todayHoursEntity.getHolidayType() == 2 && !dto.isHoliday()) {
            // 4. 정기휴무 (2) -> 영업일 (1)
            System.out.println("4. 정기휴무 (2) -> 영업일 (1)");
            // 4-1. 알림등록 임시휴무가 존재하는 경우, 변경 불가능!
            System.out.println("4-1. 알림등록 임시휴무가 존재하는 경우, 변경 불가능!");
            List<Notice> noticeList = noticeRepository.findAllByStoreId(storeId);
            String today = LocalDate.now().toString();
            for (Notice notice : noticeList) {
                if (notice.getHolidayStartDate().compareTo(today) <= 0
                        && notice.getHolidayEndDate().compareTo(today) >= 0) {
                    todayHoursEntity.updateInPerson(3, "없음", "없음", "없음");
                    return new TodayHoursRespDto(todayHoursEntity, "임시휴무");
                }
            }
            // 4-2. 알림등록 임시휴무가 존재하지 않는 경우, 변경 가능
            System.out.println("4-2. 알림등록 임시휴무가 존재하지 않는 경우, 변경 가능");
            todayHoursEntity.updateInPerson(1, dto.getBusinessHours(), dto.getBreakTime(), dto.getLastOrder());
        } else if (todayHoursEntity.getHolidayType() == 1 && dto.isHoliday()) {
            // 5. 영업일 (1) -> 임의변경 임시휴무 (4) 또는 정기휴무 (2)
            System.out.println("5. 영업일 (1) -> 임의변경 임시휴무 (4) 또는 정기휴무 (2)");
            LocalDate today = LocalDate.now();
            int weekOfMonth = today.get(WeekFields.of(Locale.getDefault()).weekOfMonth()); // 오늘 주차
            int dayOfWeek = today.getDayOfWeek().getValue() - 1; // 오늘 요일
            String holidays = holidaysRepository.mFindHolidaysByStoreId(storeId);
            if (holidays.contains(String.valueOf(weekOfMonth * 10 + dayOfWeek))) {
                // 5-1. 오늘 정기휴무인 경우
                System.out.println("5-1. 오늘 정기휴무인 경우");
                todayHoursEntity.updateInPerson(2, "없음", "없음", "없음");
            } else {
                // 5-2. 오늘 정기휴무가 아닌 경우
                System.out.println("5-2. 오늘 정기휴무가 아닌 경우");
                todayHoursEntity.updateInPerson(4, "없음", "없음", "없음");
            }
        }

        // 영업상태 설정
        String state = commonService.setState(storeId, LocalTime.now().toString().substring(0, 5),
                todayHoursEntity.getHolidayType(), todayHoursEntity.getBusinessHours(),
                todayHoursEntity.getBreakTime(), todayHoursEntity.getLastOrder());
        return new TodayHoursRespDto(todayHoursEntity, state);
    }

    @Transactional(readOnly = true)
    public TablesRespDto 잔여테이블조회(Long storeId) {
        Tables tablesEntity = tablesRepository.findByStoreId(storeId);
        return new TablesRespDto(tablesEntity);
    }

    @Transactional
    public Tables 잔여테이블수정(Long storeId, Long userId, UpdateTablesReqDto dto) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        // Tables 데이터 영속화
        Tables tableEntity = tablesRepository.findByStoreId(storeId);
        int allTableCount = tableEntity.getAllTableCount(); // 전체테이블 수

        // type: 감소 (0), 증가 (1), 초기화 (2), 임시중지 (3)
        if (dto.getType() != 3) {
            // 1. 감소/증가/초기화 요청
            if (tableEntity.isPaused()) {
                // 이미 임시중지된 경우
                return new Tables(); // 빈 Tables 객체 반환
            }
            int tableCount = tableEntity.getTableCount(); // 잔여테이블 수
            if (dto.getType() == 0) {
                if (tableCount > 0) {
                    tableCount -= 1;
                }
            } else if (dto.getType() == 1) {
                if (tableCount < allTableCount) {
                    tableCount += 1;
                }
            } else if (dto.getType() == 2) {
                tableCount = allTableCount;
            }
            tableEntity.updateTableCount(tableCount, false);
        } else {
            // 2. 임시중지 요청
            tableEntity.updateTableCount(allTableCount, dto.isPaused());
        }
        return tableEntity;
    }

    @Transactional
    public DeleteStoreRespDto 매장삭제(Long storeId, Long userId) {
        // 로그인한 사용자의 매장이 맞는지 확인
        int result = storeRepository.mCheckStoreManager(storeId, userId);
        if (result == 0) {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }

        // 대표사진 URL 리스트
        List<String> basicImageUrlList = basicImageRepository.mFindAllImageUrlByStoreId(storeId);
        // 매장내부사진 URL 리스트
        List<String> insideImageUrlList = insideImageRepository.mFindAllImageUrlByStoreId(storeId);
        // 메뉴사진 URL 리스트
        List<String> menuImageUrlList = menuImageRepository.mFindAllImageUrlByStoreId(storeId);

        // BasicImage 테이블에서 DELETE ALL
        basicImageRepository.deleteAllByStoreId(storeId);
        // InsideImage 테이블에서 DELETE ALL
        insideImageRepository.deleteAllByStoreId(storeId);
        // MenuImage 테이블에서 DELETE ALL
        menuImageRepository.deleteAllByStoreId(storeId);
        // Holidays 테이블에서 DELETE
        holidaysRepository.deleteByStoreId(storeId);
        // Hours 테이블에서 DELETE
        hoursRepository.deleteByStoreId(storeId);
        // TodayHours 테이블에서 DELETE
        todayHoursRepository.deleteByStoreId(storeId);
        // Tables 테이블에서 DELETE
        tablesRepository.deleteByStoreId(storeId);

        // 알림 첨부사진 URL 리스트
        List<String> noticeImageUrlList = new ArrayList<>();
        List<Long> noticeIdList = noticeRepository.mFindAllIdByStoreId(storeId);
        for (Long noticeId : noticeIdList) {
            noticeImageUrlList.addAll(noticeImageRepository.mFindAllImageUrlByNoticeId(noticeId));
            // NoticeImage 테이블에서 DELETE ALL
            noticeImageRepository.deleteAllByNoticeId(noticeId);
        }
        // Notice 테이블에서 DELETE ALL
        noticeRepository.deleteAllByStoreId(storeId);

        // 매장명 조회
        String storeName = storeRepository.mFindNameByStoreId(storeId);

        // Store 테이블에서 DELETE
        storeRepository.deleteById(storeId);

        // 서버에 있는 대표사진 전체 삭제
        commonService.deleteImagesOnServer("basic", basicImageUrlList);
        // 서버에 있는 매장내부사진 전체 삭제
        commonService.deleteImagesOnServer("inside", insideImageUrlList);
        // 서버에 있는 메뉴사진 전체 삭제
        commonService.deleteImagesOnServer("menu", menuImageUrlList);
        // 서버에 있는 알림 첨부사진 전체 삭제
        commonService.deleteImagesOnServer("notice", noticeImageUrlList);

        return new DeleteStoreRespDto(storeName, basicImageUrlList.size(), insideImageUrlList.size(),
                menuImageUrlList.size(), noticeIdList.size());
    }
}
