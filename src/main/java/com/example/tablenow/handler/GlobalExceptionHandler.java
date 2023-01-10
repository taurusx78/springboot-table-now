package com.example.tablenow.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.tablenow.handler.exception.CustomException;
import com.example.tablenow.handler.exception.CustomValidationException;
import com.example.tablenow.web.dto.CMRespDto;

@RestControllerAdvice // RestController에서 발생한 모든 Exception을 낚아채서 처리함
public class GlobalExceptionHandler {

	// CustomValidationException 발동 시 호출됨 (전처리)
	@ExceptionHandler(CustomValidationException.class)
	public ResponseEntity<?> validationException(CustomValidationException e) {
		return new ResponseEntity<>(
				CMRespDto.builder().code(400).message("유효성 검사 실패").response(e.getErrorMap()).build(),
				HttpStatus.BAD_REQUEST);
	}

	// CustomException 발동 시 호출됨
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<?> customException(CustomException e) {
		return new ResponseEntity<>(
				CMRespDto.builder().code(e.getCode()).message(e.getMessage()).response(e.getResponse()).build(),
				e.getHttpStatus());
	}

	// NullPointerException 발생 시 호출됨
	// 인증이 필요한 메서드를 호출할 때, 토큰이 없거나 만료된 경우 principal 변수가 Null이 되어 해당 Exception 발동함
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<?> tokenException(NullPointerException e) {
		return new ResponseEntity<>(CMRespDto.builder().code(403).message("권한 없음").build(),
				HttpStatus.FORBIDDEN);
	}

	// NoResultException 발생 시 호출됨
	// 요청한 Entity가 존재하지 않을때 해당 Exception 발동함
	@ExceptionHandler(javax.persistence.NoResultException.class)
	public ResponseEntity<?> noEntityException(javax.persistence.NoResultException e) {
		return new ResponseEntity<>(CMRespDto.builder().code(404).message("e.getMessage()").build(),
				HttpStatus.NOT_FOUND);
	}

	// org.springframework.web.multipart.MaxUploadSizeExceededException
	// 업로드한 이미지가 제한 크기를 초과하는 경우 해당 Exception 발동함
	@ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
	public ResponseEntity<?> maxUploadSizeExceededException(
			org.springframework.web.multipart.MaxUploadSizeExceededException e) {
		System.out.println("이미지 제한 크기 초과 Exception 발동");
		return new ResponseEntity<>(CMRespDto.builder().code(400).message("이미지 제한 크기 초과").build(),
				HttpStatus.BAD_REQUEST);
	}

	// 모든 Exception에 대해 해당 함수 호출됨
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> exception(Exception e) {
		System.out.println("Exception 발생");
		Map<String, Object> errorMap = new HashMap<>();
		errorMap.put("Exception Class", e.getClass());
		errorMap.put("Error Message", e.getMessage());
		return new ResponseEntity<>(
				CMRespDto.builder().code(400).message("잘못된 요청 또는 오류 발생").response(errorMap).build(),
				HttpStatus.BAD_REQUEST);
	}
}
