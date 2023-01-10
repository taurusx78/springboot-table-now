package com.example.tablenow.handler.exception;

import org.springframework.http.HttpStatus;

import lombok.Builder;

public class CustomException extends RuntimeException {

	// 객체 구분 용도
	private static final long serialVersionUID = 1L;

	private int code;
	private String response;
	private HttpStatus httpStatue;

	@Builder
	public CustomException(int code, String message, String response, HttpStatus httpStatue) {
		super(message);
		this.code = code;
		this.response = response;
		this.httpStatue = httpStatue;
	}

	public int getCode() {
		return code;
	}

	public String getResponse() {
		return response;
	}

	public HttpStatus getHttpStatus() {
		return httpStatue;
	}
}
