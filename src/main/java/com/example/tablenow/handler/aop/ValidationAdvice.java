package com.example.tablenow.handler.aop;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.tablenow.handler.exception.CustomValidationException;

@Component // @Controller 보다 늦게 Bean으로 등록돼도 됨
@Aspect // AOP를 처리하는 핸들러
public class ValidationAdvice {

    // - @Around: 지정된 메서드 실행 전 앞뒤 모두를 제어함
    // - execution 의미: 모든 접근지정자를 허용하며, web 폴더 내의
    // 'Controller'를 포함하는 클래스 내의 모든 메서드가 호출될 때 실행되도록 설정
    // - proceedingJoinPoint: 메서드의 모든 곳에 접근할 수 있는 변수
    @Around("execution(* com.example.tablenow.web..*Controller*.*(..))")
    public Object validCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult bindingResult = (BindingResult) arg;
                if (bindingResult.hasErrors()) {
                    Map<String, String> errorMap = new HashMap<>();
                    for (FieldError error : bindingResult.getFieldErrors()) {
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }
                    System.out.println(errorMap.toString());
                    throw new CustomValidationException("유효성 검사 실패", errorMap);
                }
            }
        }
        return proceedingJoinPoint.proceed(); // 메서드로 돌아감 (함수 스택 실행)
    }
}
