package com.example.tablenow.web;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tablenow.config.auth.PrincipalDetails;
import com.example.tablenow.domain.tables.Tables;
import com.example.tablenow.handler.exception.CustomException;
import com.example.tablenow.handler.exception.CustomValidationException;
import com.example.tablenow.service.store.StoreService1;
import com.example.tablenow.service.user.UserService;
import com.example.tablenow.web.dto.CMRespDto;
import com.example.tablenow.web.dto.store.StoreReqDto.CheckExistReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.SaveStoreReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateBasicReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateHolidaysReqDto;
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
import com.example.tablenow.web.dto.user.UserRepDto.CheckHoursDto;
import com.example.tablenow.web.dto.user.UserRepDto.CheckPwReqDto;

import lombok.RequiredArgsConstructor;

// 매장용 Controller

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class StoreController1 {

    private final StoreService1 storeService1;
    private final UserService userService;

    // 등록여부조회
    // 요청형식: /check/store?businessRegNum=사업자등록번호
    @GetMapping("/check/store")
    public ResponseEntity<?> checkExist(@Valid CheckExistReqDto dto, BindingResult bindingResult) {
        int result = storeService1.등록여부조회(dto); // 있으면 1, 없으면 0
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("등록 여부 조회 완료").response(result).build(),
                HttpStatus.OK);
    }

    // 매장등록
    // (참고) Multipart 타입이 포함되어 있기 때문에 @RequestBody로 못받음
    // Multipart 타입은 Validation 적용이 안되기 때문에 직접 구현해주어야 함
    @PostMapping("/manager/store")
    public ResponseEntity<?> save(@Valid SaveStoreReqDto dto, BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal) {
        // 영업시간 유효성 검사 (오픈 < 휴게 시작 < 휴게 종료 < 주문마감 < 마감)
        for (int i = 0; i < 7; i++) {
            int result = 영업시간_유효성검사(dto.getBusinessHoursList().get(i), dto.getBreakTimeList().get(i),
                    dto.getLastOrderList().get(i));
            if (result == -1) {
                throw CustomException.builder().code(400).message("유효성 검사 실패").message("영업시간을 다시 확인해 주세요.")
                        .httpStatue(HttpStatus.BAD_REQUEST).build();
            } else if (result == 0) {
                // 24시 영업인 경우, 주문마감시간 없음으로 재설정
                dto.resetLastOrder(i);
            }
        }

        // 이미지 유효성 검사
        Map<String, String> errorMap = new HashMap<>();
        if (dto.getBasicImageFileList() == null) {
            errorMap.put("basicImageFileList", "대표사진이 첨부되지 않았습니다.");
        }
        if (dto.getInsideImageFileList() == null) {
            errorMap.put("insideImageFileList", "매장내부사진이 첨부되지 않았습니다.");
        }
        if (dto.getMenuImageFileList() == null) {
            errorMap.put("menuImageFileList", "메뉴사진이 첨부되지 않았습니다.");
        }
        if (!errorMap.isEmpty()) {
            throw new CustomValidationException("이미지 유효성 검사 실패", errorMap);
        }

        SaveStoreRespDto saveStoreRespDto = storeService1.매장등록(principal.getUser(), dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(201).message("매장등록 완료").response(saveStoreRespDto).build(),
                HttpStatus.CREATED);
    }

    // 나의 매장 전체조회
    @GetMapping("/manager/store")
    public ResponseEntity<?> findAllByUserId(@AuthenticationPrincipal PrincipalDetails principal) {
        StoreListRespDto<MyStoreRespDto> myStoreRespDtoList = storeService1.나의매장전체조회(principal.getUser().getId());
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("나의 매장 전체조회 완료").response(myStoreRespDtoList).build(),
                HttpStatus.OK);
    }

    // 나의 매장 1개 조회
    @GetMapping("/manager/store/{storeId}")
    public ResponseEntity<?> findById(@PathVariable Long storeId) {
        OneMyStoreRespDto myStoreRespDto = storeService1.나의매장1개조회(storeId);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("나의 매장 1개 조회 완료").response(myStoreRespDto).build(),
                HttpStatus.OK);
    }

    // 영업시간 조회
    @GetMapping("/manager/store/{storeId}/hours")
    public ResponseEntity<?> findHours(@PathVariable Long storeId) {
        HoursRespDto hoursRespDto = storeService1.영업시간조회(storeId);
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("영업시간 조회 완료").response(hoursRespDto).build(),
                HttpStatus.OK);
    }

    // 영업시간 수정
    @PutMapping("/manager/store/{storeId}/hours")
    public ResponseEntity<?> updateHours(@PathVariable Long storeId, @Valid @RequestBody UpdateHoursReqDto dto,
            BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principal) {
        // 영업시간 유효성 검사 (오픈 < 휴게 시작 < 휴게 종료 < 주문마감 < 마감)
        for (int i = 0; i < 7; i++) {
            int result = 영업시간_유효성검사(dto.getBusinessHoursList().get(i), dto.getBreakTimeList().get(i),
                    dto.getLastOrderList().get(i));
            if (result == -1) {
                throw CustomException.builder().code(400).message("유효성 검사 실패").message("영업시간을 다시 확인해 주세요.")
                        .httpStatue(HttpStatus.BAD_REQUEST).build();
            } else if (result == 0) {
                // 24시 영업인 경우, 주문마감시간 없음으로 재설정
                dto.resetLastOrder(i);
            }
        }

        UpdateHoursRespDto updateHoursRespDto = storeService1.영업시간수정(storeId, principal.getUser().getId(), dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("영업시간 수정 완료").response(updateHoursRespDto).build(),
                HttpStatus.OK);
    }

    // 정기휴무 조회
    @GetMapping("/store/{storeId}/holidays")
    public ResponseEntity<?> findHolidays(@PathVariable Long storeId) {
        HolidaysRespDto holidaysRespDto = storeService1.정기휴무조회(storeId);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("정기휴무 조회 완료").response(holidaysRespDto).build(),
                HttpStatus.OK);
    }

    // 정기휴무 수정
    @PutMapping("/manager/store/{storeId}/holidays")
    public ResponseEntity<?> updateHolidays(@PathVariable Long storeId, @Valid @RequestBody UpdateHolidaysReqDto dto,
            BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principal) {
        UpdateHolidaysRespDto updateHolidaysRespDto = storeService1.정기휴무수정(storeId, principal.getUser().getId(),
                dto.getHolidays().trim());
        if (updateHolidaysRespDto != null) {
            return new ResponseEntity<>(
                    CMRespDto.builder().code(200).message("정기휴무 수정 완료").response(updateHolidaysRespDto).build(),
                    HttpStatus.OK);
        } else {
            throw CustomException.builder().code(403).message("권한 없음").httpStatue(HttpStatus.FORBIDDEN).build();
        }
    }

    // 메뉴 조회
    @GetMapping("/store/{storeId}/menu")
    public ResponseEntity<?> findMenu(@PathVariable Long storeId) {
        MenuRespDto menuRespDto = storeService1.메뉴조회(storeId);
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("메뉴 조회 완료").response(menuRespDto).build(),
                HttpStatus.OK);
    }

    // 메뉴 수정
    @PutMapping("/manager/store/{storeId}/menu")
    public ResponseEntity<?> updateMenu(@PathVariable Long storeId, @Valid UpdateMenuReqDto dto,
            BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principal) {
        UpdateMenuRespDto result = storeService1.메뉴수정(storeId, principal.getUser().getId(), dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("메뉴 수정 완료").response(result).build(),
                HttpStatus.OK);
    }

    // 매장내부정보 조회
    @GetMapping("/store/{storeId}/inside")
    public ResponseEntity<?> findInside(@PathVariable Long storeId) {
        InsideRespDto insideRespDto = storeService1.매장내부정보조회(storeId);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("매장내부정보 조회 완료").response(insideRespDto).build(),
                HttpStatus.OK);
    }

    // 매장내부정보 수정
    @PutMapping("/manager/store/{storeId}/inside")
    public ResponseEntity<?> updateInside(@PathVariable Long storeId, @Valid UpdateInsideReqDto dto,
            BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principal) {
        UpdateInsideRespDto result = storeService1.매장내부정보수정(storeId, principal.getUser().getId(), dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("매장내부정보 수정 완료").response(result).build(),
                HttpStatus.OK);
    }

    // 기본정보 조회
    @GetMapping("/store/{storeId}/basic")
    public ResponseEntity<?> findBasic(@PathVariable Long storeId) {
        BasicRespDto basicRespDto = storeService1.기본정보조회(storeId);
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("기본정보 조회 완료").response(basicRespDto).build(),
                HttpStatus.OK);
    }

    // 기본정보 수정
    @PutMapping("/manager/store/{storeId}/basic")
    public ResponseEntity<?> updateBasic(@PathVariable Long storeId, @Valid UpdateBasicReqDto dto,
            BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principal) {
        UpdateBasicRespDto result = storeService1.기본정보수정(storeId, principal.getUser().getId(), dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("기본정보 수정 완료").response(result).build(),
                HttpStatus.OK);
    }

    // 오늘의 영업시간 조회
    @GetMapping("/store/{storeId}/todayHours")
    public ResponseEntity<?> findTodayHours(@PathVariable Long storeId) {
        TodayHoursRespDto todayHoursRespDto = storeService1.오늘의영업시간조회(storeId);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("오늘의 영업시간 조회 완료").response(todayHoursRespDto).build(),
                HttpStatus.OK);
    }

    // 오늘의 영업시간 수정
    @PutMapping("/manager/store/{storeId}/todayHours")
    public ResponseEntity<?> updateTodayHours(@PathVariable Long storeId,
            @Valid @RequestBody UpdateTodayHoursReqDto dto, BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal) {
        // 영업시간 유효성 검사 (오픈 < 휴게 시작 < 휴게 종료 < 주문마감 < 마감)
        int result = 영업시간_유효성검사(dto.getBusinessHours(), dto.getBreakTime(), dto.getLastOrder());
        if (result == -1) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").message("영업시간을 다시 확인해 주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        } else if (result == 0) {
            // 24시 영업인 경우, 주문마감시간 없음으로 재설정
            dto.resetLastOrder();
        }

        TodayHoursRespDto todayHoursRespDto = storeService1.오늘의영업시간수정(storeId, principal.getUser().getId(), dto);
        if (todayHoursRespDto.getHolidayType() != 3) {
            return new ResponseEntity<>(
                    CMRespDto.builder().code(200).message("오늘의 영업시간 수정 완료").response(todayHoursRespDto).build(),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    CMRespDto.builder().code(417).message("오늘의 영업시간 수정 불가").response("임시휴무 알림을 수정 또는 삭제해주세요.")
                            .build(),
                    HttpStatus.EXPECTATION_FAILED);
        }

    }

    // 잔여테이블 조회
    @GetMapping("/store/{storeId}/tables")
    public ResponseEntity<?> findTables(@PathVariable Long storeId) {
        TablesRespDto tablesRespDto = storeService1.잔여테이블조회(storeId);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("잔여테이블 조회 완료").response(tablesRespDto).build(),
                HttpStatus.OK);
    }

    // 잔여테이블 수정
    @PutMapping("/manager/store/{storeId}/tables")
    public ResponseEntity<?> updateTables(@PathVariable Long storeId, @Valid @RequestBody UpdateTablesReqDto dto,
            BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principal) {
        Tables tableEntity = storeService1.잔여테이블수정(storeId, principal.getUser().getId(), dto);
        TablesRespDto tablesRespDto = new TablesRespDto(tableEntity);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("잔여테이블 수정 완료").response(tablesRespDto).build(),
                HttpStatus.OK);
    }

    // 매장삭제
    // DELETE 요청은 Headers와 Body가 없기 때문에, 비밀번호 전달을 위해 POST로 요청
    @PostMapping("/manager/store/{storeId}")
    public ResponseEntity<?> deleteById(@PathVariable Long storeId, @Valid @RequestBody CheckPwReqDto dto,
            BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principal) {
        // 회원 비밀번호 일치 여부 확인
        boolean pwMatched = userService.비밀번호확인(principal.getUser().getId(), dto);

        if (pwMatched) {
            DeleteStoreRespDto deleteStoreRespDto = storeService1.매장삭제(storeId, principal.getUser().getId());
            return new ResponseEntity<>(
                    CMRespDto.builder().code(200).message("매장삭제 완료").response(deleteStoreRespDto).build(),
                    HttpStatus.OK);
        } else {
            throw CustomException.builder().code(401).message("인증되지 않은 사용자").response("비밀번호가 일치하지 않습니다.")
                    .httpStatue(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/test")
    public void validHours(@RequestBody CheckHoursDto dto) {
        int result = 영업시간_유효성검사(dto.getBusinessHours(), dto.getBreakTime(), dto.getLastOrder());
        if (result == -1) {
            throw CustomException.builder().code(400).message("유효성 검사 실패").message("영업시간을 다시 확인해 주세요.")
                    .httpStatue(HttpStatus.BAD_REQUEST).build();
        } else if (result == 0) {
            // 24시 영업인 경우, 주문마감시간 없음으로 재설정
            dto.resetLastOrder();
        }
        System.out.println("주문마감시간: " + dto.getLastOrder());
    }

    public int 영업시간_유효성검사(String businessHours, String breakTime, String lastOrder) {
        String[] businessHoursPart = businessHours.split(" - ");
        int open = timeToInt(-1, businessHoursPart[0]);
        int close = timeToInt(open, businessHoursPart[1]);
        // 24시 영업인 경우
        if (open == close) {
            return 0;
        }

        int breakStart = open + 1;
        int breakEnd = breakStart + 1;
        if (!breakTime.equals("없음")) {
            String[] breakTimePart = breakTime.split(" - ");
            breakStart = timeToInt(open, breakTimePart[0]);
            breakEnd = timeToInt(open, breakTimePart[1]);
        }
        int last = close;
        if (!lastOrder.equals("없음")) {
            last = timeToInt(open, lastOrder);
        }

        System.out.println(open + ", " + breakStart + ", " + breakEnd + ", " + last + ", " + close);
        if (open < breakStart && breakStart < breakEnd && breakEnd < last && last <= close) {
            return 1; // 유효성검사 통과
        }
        return -1; // 유효성검사 실패
    }

    // 시간을 정수로 변환 (ex. 10:30 -> 1030), 오픈시간 보다 작은 경우 +2400
    public int timeToInt(int open, String time) {
        String[] timeList = time.split(":");
        int result = Integer.parseInt(timeList[0]) * 100 + Integer.parseInt(timeList[1]);
        if (result < open) {
            result += 2400;
        }
        return result;
    }
}
