package com.example.tablenow.web.dto.sms;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 네이버 SENS SMS 인증번호 요청에 대한 응답 결과를 담는 DTO

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SmsRespDto {

	private String statusCode;
	private String statusName;
	private String requestId;
	private Timestamp requestTime;
}
