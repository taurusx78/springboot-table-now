package com.example.tablenow.web.dto.sms;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 네이버 SENS SMS 인증번호 요청 DTO

@Builder
@AllArgsConstructor
@Getter
public class SmsReqDto {

	private String type; // HTTP 요청 타입
	private String contentType; // HTTP 헤더 Content-Type
	private String countryCode; // 국가번호
	private String from; // 발신 전화번호
	private String content; // 메세지 내용
	private List<Map<String, String>> messages; // 수신자 전화번호
}
