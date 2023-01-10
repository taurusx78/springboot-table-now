package com.example.tablenow.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CMRespDto<T> {

	private int code;
	private String message;
	private T response;

	@Builder
	public CMRespDto(int code, String message, T response) {
		this.code = code;
		this.message = message;
		this.response = response;
	}
}