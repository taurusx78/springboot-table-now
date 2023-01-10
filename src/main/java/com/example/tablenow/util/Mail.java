package com.example.tablenow.util;

import java.util.Random;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class Mail {

    private final JavaMailSender javaMailSender;

    // 이메일 전송
    public String sendEmail(String email) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        String randomNumber = createRandomNumber();

        message.addRecipients(RecipientType.TO, email); // 받는 주소
        message.setSubject("이메일 인증번호가 도착했습니다."); // 메일 제목

        String content = "<div style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; font-size: 15px; \r\n"
                + " width: 550px; border-top: 4px solid #03AAF1; border-bottom: 4px solid #03AAF1;\r\n"
                + " margin: 30px auto; padding: 50px 10px; line-height: 30px;\">";
        content += "<h1 style=\"font-size: 28px; font-weight: 400;\">\r\n"
                + " <span style=\"font-size: 14px;\">테이블나우</span><br>\r\n"
                + " <span style=\"color: #03AAF1; margin-left: -2px;\">메일인증</span> 안내입니다.</h1>";
        content += "<p style=\"margin-top: 50px;\">\r\n" + " 안녕하세요. 테이블나우입니다.<br>\r\n"
                + " 아래의 <b style=\"color: #03AAF1;\">인증번호 6자리</b>를 입력하고 메일 인증을 완료해주세요.<br>\r\n" + " 감사합니다!</p>";
        content += "<p style=\"width: 150px; height: 45px; margin-top: 50px; background: #03AAF1;\r\n"
                + " line-height: 45px; text-align: center; color: white; font-size: 20px;\">";
        content += randomNumber + "</p></div>";

        message.setText(content, "utf-8", "html"); // 메일 내용
        message.setFrom(new InternetAddress("taurusx@naver.com", "테이블나우")); // 보내는 주소

        javaMailSender.send(message);

        System.out.println("인증번호 : " + randomNumber);
        return randomNumber;
    }

    // 휴대폰 메세지 전송
    public String sendSms(String phone) throws Exception {
        NaverSens sms = new NaverSens();
        String randomNumber = createRandomNumber();

        String content = "\n안녕하세요! 테이블나우입니다.\n인증번호 [" + randomNumber + "]를 입력해주세요.";
        sms.sendSms(phone, content);

        System.out.println("인증번호 : " + randomNumber);
        return randomNumber;
    }

    // 인증번호 생성 (6자리 난수)
    public String createRandomNumber() {
        Random random = new Random();
        String randomNumber = "";
        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(random.nextInt(10));
        }
        return randomNumber;
    }
}
