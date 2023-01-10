package com.example.tablenow.service.store;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tablenow.domain.holidays.HolidaysRepository;
import com.example.tablenow.domain.hours.Hours;
import com.example.tablenow.domain.hours.HoursRepository;
import com.example.tablenow.domain.notice.Notice;
import com.example.tablenow.domain.store.Store;
import com.example.tablenow.domain.store.StoreRepository;
import com.example.tablenow.domain.today_hours.TodayHours;
import com.example.tablenow.domain.today_hours.TodayHoursRepository;
import com.example.tablenow.util.NaverBlog;
import com.example.tablenow.web.dto.notice.NoticeRespDto;
import com.example.tablenow.web.dto.store.DBStoreRespDto.SelectStateRespDto;
import com.example.tablenow.web.dto.store.DBStoreRespDto.SelectStoreRespDto;
import com.example.tablenow.web.dto.store.DBStoreRespDto.SelectUserRespDto;
import com.example.tablenow.web.dto.store.StoreReqDto.SpecificStoreReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateInfoReqDto;
import com.example.tablenow.web.dto.store.StoreRespDto.BlogRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.DetailsRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.StoreListRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.NameRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.SpecificStoreRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.StateRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.WeeklyHoursRespDto;

import lombok.RequiredArgsConstructor;

// 고객용 StoreService

@RequiredArgsConstructor
@Service
public class StoreService2 {

    private final StoreRepository storeRepository;
    private final HolidaysRepository holidaysRepository;
    private final HoursRepository hoursRepository;
    private final TodayHoursRepository todayHoursRepository;

    private final StoreCommonService commonService;
    private final EntityManager em; // 네이티브 쿼리를 작성하기 위해 필요함
    private final JavaMailSender javaMailSender;

    @Transactional(readOnly = true)
    public StoreListRespDto<NameRespDto> 매장명전체조회() {
        // SELECT하는 데이터 타입이 Store가 아닌 NameRespDto이기 때문에
        // StoreRepository 사용 못하고 직접 쿼리를 작성해 주어야 함
        String sql = "SELECT id, name FROM store";
        Query query = em.createNativeQuery(sql);
        JpaResultMapper result = new JpaResultMapper();
        List<NameRespDto> dtoList = result.list(query, NameRespDto.class);
        return new StoreListRespDto<>(dtoList);
    }

    // (주의) TodayHours 테이블 UPDATE 가능성 있기 때문에 readOnly=true 설정하면 안됨!
    @Transactional
    public StoreListRespDto<SpecificStoreRespDto> 즐겨찾기전체조회(List<Long> storeIds) {
        // SELECT하는 데이터 타입이 Store가 아닌 SelectStoreRespDto이기 때문에
        // StoreRepository 사용 못하고 직접 쿼리를 작성해 주어야 함
        String sql = "SELECT s.id, s.name, s.jibunAddress, b.imageUrl AS basicImageUrl, t.today, t.holidayType, "
                + "t.businessHours, t.breakTime, t.lastOrder, t.modifiedDate AS todayHoursModified, "
                + "tb.tableCount, tb.paused, tb.modifiedDate AS tablesModified "
                + "FROM store s, basic_image b, today_hours t, tables tb "
                + "WHERE b.storeId = s.id AND t.storeId = s.id AND tb.storeId = s.id AND s.id IN (:storeIds) "
                + "GROUP BY s.id ORDER BY FIELD(s.id, :storeIds)";
        Query query = em.createNativeQuery(sql).setParameter("storeIds", storeIds);
        JpaResultMapper result = new JpaResultMapper();
        List<SelectStoreRespDto> selectDtoList = result.list(query, SelectStoreRespDto.class);

        List<SpecificStoreRespDto> dtoList = 매장전체조회(selectDtoList);
        return new StoreListRespDto<>(dtoList);
    }

    // (주의) TodayHours 테이블 UPDATE 가능성 있기 때문에 readOnly=true 설정하면 안됨!
    @Transactional
    public StoreListRespDto<SpecificStoreRespDto> 검색매장전체조회(SpecificStoreReqDto dto) {
        String sql = "SELECT s.id, s.name, s.jibunAddress, b.imageUrl AS basicImageUrl, t.today, t.holidayType, "
                + "t.businessHours, t.breakTime, t.lastOrder, t.modifiedDate AS todayHoursModified, "
                + "tb.tableCount, tb.paused, tb.modifiedDate AS tablesModified, "
                + "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) "
                + "- radians(:longitude)) + sin (radians(:latitude)) * sin(radians(latitude)))) AS distance "
                + "FROM store s, basic_image b, today_hours t, tables tb "
                + "WHERE b.storeId = s.id AND t.storeId = s.id AND tb.storeId = s.id "
                + "AND s.name LIKE CONCAT('%', :name, '%') GROUP BY s.id";
        Query query = em.createNativeQuery(sql).setParameter("latitude", dto.getLatitude())
                .setParameter("longitude", dto.getLongitude()).setParameter("name", dto.getData());
        JpaResultMapper result = new JpaResultMapper();
        List<SelectStoreRespDto> selectDtoList = result.list(query, SelectStoreRespDto.class);

        List<SpecificStoreRespDto> dtoList = 매장전체조회(selectDtoList);
        return new StoreListRespDto<>(dtoList);
    }

    // (주의) TodayHours 테이블 UPDATE 가능성 있기 때문에 readOnly=true 설정하면 안됨!
    @Transactional
    public StoreListRespDto<SpecificStoreRespDto> 카테고리매장전체조회(SpecificStoreReqDto dto) {
        int length = 500;// 반경 몇 km 까지 조회할 지 설정
        String sql = "SELECT s.id, s.name, s.jibunAddress, b.imageUrl AS basicImageUrl, t.today, t.holidayType, "
                + "t.businessHours, t.breakTime, t.lastOrder, t.modifiedDate AS todayHoursModified, "
                + "tb.tableCount, tb.paused, tb.modifiedDate AS tablesModified, "
                + "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) "
                + "- radians(:longitude)) + sin (radians(:latitude)) * sin(radians(latitude)))) AS distance "
                + "FROM store s, basic_image b, today_hours t, tables tb "
                + "WHERE b.storeId = s.id AND t.storeId = s.id AND tb.storeId = s.id "
                + "AND s.category = :category GROUP BY s.id HAVING distance < :length";
        Query query = em.createNativeQuery(sql).setParameter("latitude", dto.getLatitude())
                .setParameter("longitude", dto.getLongitude()).setParameter("category", dto.getData())
                .setParameter("length", length);
        JpaResultMapper result = new JpaResultMapper();
        List<SelectStoreRespDto> selectDtoList = result.list(query, SelectStoreRespDto.class);

        List<SpecificStoreRespDto> dtoList = 매장전체조회(selectDtoList);
        return new StoreListRespDto<>(dtoList);
    }

    // (주의) TodayHours 테이블 UPDATE 가능성 있기 때문에 readOnly=true 설정하면 안됨!
    @Transactional
    public List<SpecificStoreRespDto> 매장전체조회(List<SelectStoreRespDto> selectDtoList) {
        List<SpecificStoreRespDto> dtoList = new ArrayList<>();
        LocalDate today = LocalDate.now(); // 오늘 날짜

        for (SelectStoreRespDto store : selectDtoList) {
            // 영업상태 설정
            String state;
            // 오늘의영업시간 업데이트 여부 확인
            if (store.getToday().toLocalDate().isBefore(today)) {
                // TodayHours 테이블 UPDATE
                TodayHours todayHours = commonService.오늘의영업시간수정_날짜변경(today, store.getId().longValue());
                state = commonService.setState(store.getId(), LocalTime.now().toString().substring(0, 5),
                        todayHours.getHolidayType(),
                        todayHours.getBusinessHours(), todayHours.getBreakTime(), todayHours.getLastOrder());
            } else {
                state = commonService.setState(store.getId(), LocalTime.now().toString().substring(0, 5),
                        store.getHolidayType(),
                        store.getBusinessHours(), store.getBreakTime(), store.getLastOrder());
            }

            SpecificStoreRespDto dto = new SpecificStoreRespDto(store, state);
            dtoList.add(dto);
        }
        return dtoList;
    }

    // (주의) TodayHours 테이블 UPDATE 가능성 있기 때문에 readOnly=true 설정하면 안됨!
    @Transactional
    public DetailsRespDto 매장상세조회(Long storeId) throws Exception {
        Store storeEntity = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NoResultException("존재하지 않는 매장");
        });

        // 1. 오늘의영업시간 업데이트 여부 확인
        TodayHours todayHours = storeEntity.getTodayHours();
        LocalDate today = LocalDate.now(); // 오늘 날짜
        if (todayHours.getToday().isBefore(today)) {
            // TodayHours 테이블 UPDATE
            todayHours = commonService.오늘의영업시간수정_날짜변경(today, storeId);
        }

        // 2. 영업상태 설정
        String state = commonService.setState(storeId, LocalTime.now().toString().substring(0, 5),
                todayHours.getHolidayType(),
                todayHours.getBusinessHours(), todayHours.getBreakTime(), todayHours.getLastOrder());

        // 3. NoticeRespDto 리스트 생성
        List<NoticeRespDto> noticeList = new ArrayList<>();
        for (Notice notice : storeEntity.getNoticeList()) {
            NoticeRespDto noticeRespDto = new NoticeRespDto(notice, notice.getNoticeImageList());
            noticeList.add(noticeRespDto);
        }

        // 4. 네이버 블로그 리뷰 BlogRespDto 리스트 생성
        NaverBlog naverBlog = new NaverBlog();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser
                .parse(naverBlog.search(storeEntity.getJibunAddress().split(" ")[1] + " " + storeEntity.getName()));
        JSONArray jsonArray = (JSONArray) jsonObject.get("items");
        List<BlogRespDto> blogList = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0, len = jsonArray.size(); i < len; i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                blogList.add(BlogRespDto.builder().title(removeHtmlTag(object.get("title")))
                        .description(removeHtmlTag(object.get("description")))
                        .bloggername(removeHtmlTag(object.get("bloggername")))
                        .postdate(removeHtmlTag(object.get("postdate"))).link(removeHtmlTag(object.get("link")))
                        .build());
            }
        }

        DetailsRespDto dto = new DetailsRespDto(storeEntity, state, noticeList, blogList);
        return dto;
    }

    // HTML 태그 제거
    public String removeHtmlTag(Object html) {
        return html.toString().replaceAll("<[^>]*>", "").replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&nbsp;", " ").replaceAll("&amp;", "&").replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'");
    }

    @Transactional(readOnly = true)
    public WeeklyHoursRespDto 영업시간전체조회(Long storeId) {
        // 영업시간 조회
        List<Hours> hoursEntityList = hoursRepository.findAllByStoreId(storeId);
        // 정기휴무 조회
        String[] holidayList = holidaysRepository.mFindHolidaysByStoreId(storeId).split(" ");

        // 요일별 [영업시간, 휴게시간, 주문마감시간, 휴무 주차]
        String[][] weeklyHours = new String[7][4];

        // 영업시간 반영
        for (int i = 0; i < 7; i++) {
            weeklyHours[i][0] = hoursEntityList.get(i).getBusinessHours();
            weeklyHours[i][1] = hoursEntityList.get(i).getBreakTime();
            weeklyHours[i][2] = hoursEntityList.get(i).getLastOrder();
            weeklyHours[i][3] = "";
        }

        List<Integer> completeDayType = new ArrayList<>(); // 처리 완료된 요일

        // 정기휴무 반영
        for (String holiday : holidayList) {
            if (holiday.isEmpty())
                break;

            int week = holiday.charAt(0) - '0'; // 주차
            int dayType = holiday.charAt(1) - '0'; // 요일 (0~6)

            // 이미 처리 완료된 요일인 경우 패스
            if (completeDayType.contains(dayType))
                continue;

            // 매주 휴무 여부
            if (week == 0) {
                weeklyHours[dayType][3] = "매주";
                completeDayType.add(dayType);
            } else {
                weeklyHours[dayType][3] += week + "째주 ";
            }
        }

        return new WeeklyHoursRespDto(weeklyHours);
    }

    // (주의) TodayHours 테이블 UPDATE 가능성 있기 때문에 readOnly=true 설정하면 안됨!
    @Transactional
    public StateRespDto 영업상태조회(Long storeId) {
        String sql = "SELECT t.holidayType, t.businessHours, t.breakTime, t.lastOrder, t.modifiedDate AS todayModified, "
                + "tb.tableCount, tb.paused, tb.modifiedDate AS tablesModified "
                + "FROM today_hours t INNER JOIN tables tb "
                + "ON t.storeId = tb.storeId WHERE t.storeId = :storeId";
        Query query = em.createNativeQuery(sql).setParameter("storeId", storeId);
        JpaResultMapper result = new JpaResultMapper();
        SelectStateRespDto selectDto = result.uniqueResult(query, SelectStateRespDto.class);

        // 영업상태 업데이트
        StateRespDto stateRespDto = 영업상태업데이트(storeId, selectDto);
        return stateRespDto;
    }

    @Transactional
    public StateRespDto 영업상태업데이트(Long storeId, SelectStateRespDto selectDto) {
        String state = "";
        Date dbToday = todayHoursRepository.mFindTodayByStoreId(storeId);
        LocalDate today = LocalDate.now(); // 오늘 날짜
        if (dbToday.toLocalDate().isBefore(today)) {
            // 날짜가 변경된 경우, TodayHours UPDATE
            TodayHours todayHours = commonService.오늘의영업시간수정_날짜변경(today, storeId);
            state = commonService.setState(storeId, LocalTime.now().toString().substring(0, 5),
                    todayHours.getHolidayType(),
                    todayHours.getBusinessHours(), todayHours.getBreakTime(), todayHours.getLastOrder());
        } else {
            state = commonService.setState(storeId, LocalTime.now().toString().substring(0, 5),
                    selectDto.getHolidayType(),
                    selectDto.getBusinessHours(), selectDto.getBreakTime(), selectDto.getLastOrder());
        }
        return new StateRespDto(selectDto, state);
    }

    @Transactional(readOnly = true)
    public void 매장정보수정제안(Long storeId, UpdateInfoReqDto dto)
            throws MessagingException, UnsupportedEncodingException {
        String sql = "SELECT s.name AS storeName, u.name AS managerName, u.email "
                + "FROM store s INNER JOIN users u "
                + "ON s.userId = u.id WHERE s.id = :storeId";
        Query query = em.createNativeQuery(sql).setParameter("storeId", storeId);
        JpaResultMapper result = new JpaResultMapper();
        SelectUserRespDto selectDto = result.uniqueResult(query, SelectUserRespDto.class);

        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(RecipientType.TO, selectDto.getEmail()); // 받는 주소
        message.setSubject("[" + selectDto.getStoreName() + "] 매장 정보 수정을 제안합니다."); // 메일 제목

        String content = "<div style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; font-size: 14px;";
        content += "width: 550px; border-top: 4px solid #03AAF1; border-bottom: 4px solid #03AAF1;";
        content += "margin: 30px auto; padding: 50px 10px; line-height: 30px;\">";

        content += "<h1 style=\"font-size: 25px; font-weight: 400;\">";
        content += "<span style=\"font-size: 12px;\">테이블나우</span><br>";
        content += "<span style=\"color: #03AAF1; margin-left: -2px;\">정보수정 제안</span> 메일입니다.</h1>";

        content += "<p style=\"margin-top: 50px;\">안녕하세요. " + selectDto.getManagerName() + " 사장님! 테이블나우입니다.<br>";
        content += "<b style=\"color: #03AAF1;\">" + selectDto.getStoreName() + "</b>";
        content += "를 이용하시는 고객님께서 아래 항목에 대한 정보수정을 제안하셨습니다.<br></p>";

        content += "<div  style=\"margin-top: 50px; padding: 20px 10px; border-top: 1px solid lightgray;";
        content += "border-bottom: 1px solid lightgray; font-size: 14px;\">";

        // 매장정보 목록
        String[] infoItems = { "영업시간", "메뉴/가격", "매장내부사진", "매장 폐업" };
        // 인덱스 중복 제거
        List<Integer> indexList = dto.getInfoIndexList().stream().distinct().collect(Collectors.toList());

        for (int index : indexList) {
            content += "o  " + infoItems[index] + "<br>";
        }
        content += "</div><p style=\"margin-top: 50px;\">";

        content += "제안된 항목을 확인하시고 정보가 변경된 경우 이를 반영해주세요.<br>";
        content += "매장에 대한 고객의 만족도를 높일 수 있습니다.<br></p>";
        content += "<p>테이블나우를 이용해주셔서 항상 감사합니다!</p>";

        message.setText(content, "utf-8", "html"); // 메일 내용
        message.setFrom(new InternetAddress("taurusx@naver.com", "테이블나우")); // 보내는 주소

        javaMailSender.send(message);
    }
}
