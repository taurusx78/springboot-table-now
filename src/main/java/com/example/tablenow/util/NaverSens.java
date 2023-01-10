package com.example.tablenow.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.example.tablenow.config.SecretProperties;
import com.example.tablenow.web.dto.sms.SmsReqDto;
import com.example.tablenow.web.dto.sms.SmsRespDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NaverSens {

	public void sendSms(String phone, String content) throws Exception {
		String timestamp = Long.toString(System.currentTimeMillis()); // current timestamp (epoch)
		URI uri = new URI(
				"https://sens.apigw.ntruss.com/sms/v2/services/" + SecretProperties.SENS_SERVICE_ID + "/messages");

		// 수신자 전화번호
		List<Map<String, String>> messages = new ArrayList<>();
		Map<String, String> map = Map.of("to", phone);
		messages.add(map);

		// Body에 담을 JSON 데이터 생성
		SmsReqDto smsReqDto = SmsReqDto.builder().type("SMS").contentType("COMM").countryCode("82")
				.from(SecretProperties.SEND_PHONE).content(content).messages(messages).build();
		ObjectMapper om = new ObjectMapper();
		String jsonBody = om.writeValueAsString(smsReqDto);

		// HTTP 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x-ncp-apigw-timestamp", timestamp);
		headers.set("x-ncp-iam-access-key", SecretProperties.NCP_ACCESS_KEY);
		headers.set("x-ncp-apigw-signature-v2", makeSignature(timestamp));

		// HTTP 헤더와 jsonBody 조합
		HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
		System.out.println(entity.getHeaders());
		System.out.println(entity.getBody());

		// RestTemplate로 POST 요청
		RestTemplate restTemplate = new RestTemplate();
		SmsRespDto smsRespDto = restTemplate.postForObject(uri, entity, SmsRespDto.class);

		// 응답 결과 코드 출력
		if (smsRespDto != null) {
			System.out.println("응답결과: " + smsRespDto.getStatusCode());
		}
	}

	public String makeSignature(String timestamp) throws Exception {
		String space = " ";
		String newLine = "\n";
		String method = "POST";
		String url = "/sms/v2/services/" + SecretProperties.SENS_SERVICE_ID + "/messages";
		String accessKey = SecretProperties.NCP_ACCESS_KEY;
		String secretKey = SecretProperties.NCP_SECRET_KEY;

		String message = new StringBuilder().append(method).append(space).append(url).append(newLine).append(timestamp)
				.append(newLine).append(accessKey).toString();

		SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(signingKey);
		byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
		String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

		return encodeBase64String;
	}
}
