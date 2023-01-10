package com.example.tablenow.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tablenow.service.store.StoreService2;
import com.example.tablenow.web.dto.CMRespDto;
import com.example.tablenow.web.dto.store.StoreReqDto.SpecificStoreReqDto;
import com.example.tablenow.web.dto.store.StoreReqDto.UpdateInfoReqDto;
import com.example.tablenow.web.dto.store.StoreRespDto.DetailsRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.StoreListRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.NameRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.SpecificStoreRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.StateRespDto;
import com.example.tablenow.web.dto.store.StoreRespDto.WeeklyHoursRespDto;

import lombok.RequiredArgsConstructor;

// 고객용 Controller

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class StoreController2 {

    private final StoreService2 storeService2;

    // 매장명 전체조회 (연관검색어 생성용)
    @GetMapping("/names")
    public ResponseEntity<?> findAllName() {
        StoreListRespDto<NameRespDto> nameRespDtoList = storeService2.매장명전체조회();
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("매장명 전체조회 완료").response(nameRespDtoList).build(),
                HttpStatus.OK);
    }

    // 즐겨찾기 전체조회
    // 요청형식: /bookmark?storeIds=3,2
    @GetMapping("/bookmark")
    public ResponseEntity<?> findAllBookmark(@RequestParam List<Long> storeIds) {
        StoreListRespDto<SpecificStoreRespDto> storeRespDtoList = storeService2.즐겨찾기전체조회(storeIds);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("즐겨찾기 전체조회 완료").response(storeRespDtoList).build(),
                HttpStatus.OK);
    }

    // 검색매장 전체조회
    // 요청형식: /search?data=동탄&latitude=37.200455&longitude=127.075289
    @GetMapping("/search")
    public ResponseEntity<?> findAllByName(@Valid SpecificStoreReqDto dto, BindingResult bindingResult) {
        StoreListRespDto<SpecificStoreRespDto> storeRespDtoList = storeService2.검색매장전체조회(dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("검색 매장 전체조회 완료").response(storeRespDtoList).build(),
                HttpStatus.OK);
    }

    // 카테고리 매장 전체조회
    // 요청형식: /category?data=카페&latitude=37.200455&longitude=127.075289
    @GetMapping("/category")
    public ResponseEntity<?> findAllByCategory(@Valid SpecificStoreReqDto dto, BindingResult bindingResult) {
        StoreListRespDto<SpecificStoreRespDto> storeRespDtoList = storeService2.카테고리매장전체조회(dto);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("카테고리 매장 전체조회 완료").response(storeRespDtoList).build(),
                HttpStatus.OK);
    }

    // 매장 상세조회
    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> findById(@PathVariable Long storeId) throws Exception {
        DetailsRespDto detailsRespDto = storeService2.매장상세조회(storeId);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("매장 상세조회 완료").response(detailsRespDto).build(),
                HttpStatus.OK);
    }

    // 영업시간 조회
    @GetMapping("/store/{storeId}/hours")
    public ResponseEntity<?> findHours(@PathVariable Long storeId) {
        WeeklyHoursRespDto weeklyHoursRespDto = storeService2.영업시간전체조회(storeId);
        return new ResponseEntity<>(
                CMRespDto.builder().code(200).message("(고객용) 영업시간 조회 완료").response(weeklyHoursRespDto).build(),
                HttpStatus.OK);
    }

    // 영업상태 조회
    @GetMapping("/store/{storeId}/state")
    public ResponseEntity<?> updateState(@PathVariable Long storeId) {
        StateRespDto stateRespDto = storeService2.영업상태조회(storeId);
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("영업상태 조회 완료").response(stateRespDto).build(),
                HttpStatus.OK);
    }

    @PostMapping("/store/{storeId}/change")
    public ResponseEntity<?> requestUpdate(@PathVariable Long storeId, @Valid @RequestBody UpdateInfoReqDto dto,
            BindingResult bindingResult) throws Exception {
        storeService2.매장정보수정제안(storeId, dto);
        return new ResponseEntity<>(CMRespDto.builder().code(200).message("매장정보 수정제안 완료").build(),
                HttpStatus.OK);
    }
}
