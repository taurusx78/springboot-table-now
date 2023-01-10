package com.example.tablenow.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 서버 저장소에 있는 이미지를 불러오는 컨트롤러
// (참고) https://dev-gorany.tistory.com/17

@RequestMapping("/api/v1")
@RestController
public class ImageController {

	@GetMapping("/image")
	public ResponseEntity<Resource> findImage(@Param("type") String type, @Param("filename") String filename)
			throws IOException {
		String path = "C:\\sts-4.14.0.RELEASE\\workspace\\images\\";
		Resource resource = new FileSystemResource(path + type + "\\" + filename);

		// 파일이 존재하지 않으면 Exception 발동
		if (!resource.exists()) {
			throw new RuntimeException("이미지가 존재하지 않습니다.");
		}

		Path filePath = Paths.get(path + type + "\\" + filename);
		HttpHeaders headers = new HttpHeaders();
		// 파일의 확장자명(jpg, png)에 따라 Content-Type 명시
		headers.add("Content-Type", Files.probeContentType(filePath));

		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}
}
