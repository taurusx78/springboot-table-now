package com.example.tablenow.web;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tablenow.config.auth.PrincipalDetails;
import com.example.tablenow.handler.exception.CustomException;
import com.example.tablenow.service.notice.NoticeService;
import com.example.tablenow.web.dto.CMRespDto;
import com.example.tablenow.web.dto.notice.NoticeReqDto.SaveNoticeReqDto;
import com.example.tablenow.web.dto.notice.NoticeRespDto.NoticeListRespDto;
import com.example.tablenow.web.dto.notice.NoticeRespDto.SaveNoticeRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.TodayHoursRespDto;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class NoticeController {

    private final NoticeService noticeService;

    // 알림등록
    // (참고) Multipart 타입이 포함되어 있기 때문에 @RequestBody로 못받음
    // Multipart 타입은 Validation 적용이 안되기 때문에 직접 구현해주어야 함
    @PostMapping("/manager/store/{storeId}/notice")
    public ResponseEntity<?> save(@PathVariable Long storeId, @Valid SaveNoticeReqDto dto,
            BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principal) {
        // 임시휴무 유효성 검사 (시작/종료일 중 하나만 공백 or 시작일 > 종료일 or 이미 종료됨)
        if (!dto.getHolidayStartDate().equals("") || !dto.getHolidayEndDate().equals("")) {
            if (dto.getHolidayStartDate().equals("") ^ dto.getHolidayEndDate().equals("")
                    || dto.getHolidayStartDate().compareTo(dto.getHolidayEndDate()) > 0
                    || dto.getHolidayEndDate().compareTo(LocalDate.now().toString()) < 0) {
                throw CustomException.builder().code(400).message("유효성 검사 실패").response("임시휴무 날짜를 다시 확인해 주세요.")
                        .httpStatue(HttpStatus.BAD_REQUEST).build();
            }
        }

        SaveNoticeRespDto saveNoticeRespDto = noticeService.알림등록(storeId, principal.getUser().getId(), dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(201).message("알림등록 완료").response(saveNoticeRespDto).build(),
                HttpStatus.CREATED);
    }

    // 알림 전체조회
    @GetMapping("/store/{storeId}/notice")
    public ResponseEntity<?> findAll(@PathVariable Long storeId) {
        NoticeListRespDto noticeRespDtoList = noticeService.알림전체조회(storeId);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("알림 전체조회 완료").response(noticeRespDtoList).build(),
                HttpStatus.OK);
    }

    // 알림수정
    @PutMapping("/manager/store/{storeId}/notice/{noticeId}")
    public ResponseEntity<?> updateById(@PathVariable Long storeId, @PathVariable Long noticeId,
            @Valid SaveNoticeReqDto dto, BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principal) {
        // 임시휴무 유효성 검사 (시작/종료일 중 하나만 공백 or 시작일 > 종료일 or 이미 종료됨)
        if (!dto.getHolidayStartDate().equals("") || !dto.getHolidayEndDate().equals("")) {
            if (dto.getHolidayStartDate().equals("") ^ dto.getHolidayEndDate().equals("")
                    || dto.getHolidayStartDate().compareTo(dto.getHolidayEndDate()) > 0
                    || dto.getHolidayEndDate().compareTo(LocalDate.now().toString()) < 0) {
                throw CustomException.builder().code(400).message("유효성 검사 실패").response("임시휴무 날짜를 다시 확인해 주세요.")
                        .httpStatue(HttpStatus.BAD_REQUEST).build();
            }
        }

        SaveNoticeRespDto saveNoticeRespDto = noticeService.알림수정(storeId, principal.getUser().getId(), noticeId, dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("알림수정 완료").response(saveNoticeRespDto).build(),
                HttpStatus.OK);
    }

    // 알림삭제
    @DeleteMapping("/manager/store/{storeId}/notice/{noticeId}")
    public ResponseEntity<?> deleteById(@PathVariable Long storeId, @PathVariable Long noticeId,
            @AuthenticationPrincipal PrincipalDetails principal) {
        TodayHoursRespDto todayHoursRespDto = noticeService.알림삭제(storeId, principal.getUser().getId(), noticeId);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("알림삭제 완료").response(todayHoursRespDto).build(),
                HttpStatus.OK);
    }
}
