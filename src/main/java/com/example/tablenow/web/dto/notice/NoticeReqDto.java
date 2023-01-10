package com.example.tablenow.web.dto.notice;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.example.tablenow.domain.notice.Notice;
import com.example.tablenow.domain.notice_image.NoticeImage;
import com.example.tablenow.domain.store.Store;

import lombok.Data;

public class NoticeReqDto {

    @Data
    public static class SaveNoticeReqDto {

        @NotBlank(message = "알림 제목은 필수항목입니다.")
        @Size(max = 50, message = "알림 제목은 50자 이내로 입력해주세요.")
        private String title; // 제목

        @NotBlank(message = "알림 내용은 필수항목입니다.")
        @Size(max = 500, message = "알림 내용은 500자 이내로 입력해주세요.")
        private String content; // 내용

        @NotNull(message = "임시휴무 시작일은 Null일 수 없습니다.") // 공백은 가능
        @Pattern(regexp = "(20\\d{2}-[0-1][0-9]-[0-3][0-9])|()", message = "날짜는 yyyy-MM-DD 형식으로 입력해 주세요.")
        private String holidayStartDate; // 임시휴무 시작일

        @NotNull(message = "임시휴무 종료일은 Null일 수 없습니다.") // 공백은 가능
        @Pattern(regexp = "(20\\d{2}-[0-1][0-9]-[0-3][0-9])|()", message = "날짜는 yyyy-MM-DD 형식으로 입력해 주세요.")
        private String holidayEndDate; // 임시휴무 종료일

        private List<MultipartFile> addedImageFileList; // 추가된 첨부사진 파일 리스트

        private List<String> deletedImageUrlList; // 삭제된 첨부사진 URL 리스트

        // Notice 오브젝트 생성
        public Notice toNoticeEntity(Store store) {
            return Notice.builder().title(title).content(content).holidayStartDate(holidayStartDate)
                    .holidayEndDate(holidayEndDate).store(store).build();
        }

        // NoticeImage 오브젝트 생성
        public NoticeImage toNoticeImageEntity(String imageUrl, Notice notice) {
            return NoticeImage.builder().imageUrl(imageUrl).notice(notice).build();
        }
    }
}
