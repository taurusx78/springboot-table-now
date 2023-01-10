package com.example.tablenow.service.user;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tablenow.domain.store.StoreRepository;
import com.example.tablenow.domain.user.User;
import com.example.tablenow.domain.user.UserRepository;
import com.example.tablenow.handler.exception.CustomException;
import com.example.tablenow.service.store.StoreService1;
import com.example.tablenow.web.dto.user.UserRepDto.ChangeEmailReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.ChangePwReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.CheckJoinedReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.CheckPwReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.CheckUsernameReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.JoinReqDto;
import com.example.tablenow.web.dto.user.UserRepDto.ResetPwReqDto;
import com.example.tablenow.web.dto.user.UserRespDto;
import com.example.tablenow.web.dto.user.UserRespDto.FindIdRespDto;
import com.example.tablenow.web.dto.user.UserRespDto.WithdrawalRespDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder encoder;
	private final StoreRepository storeRepository;
	private final StoreService1 storeService1;

	@Transactional(readOnly = true)
	public int 가입여부조회(CheckJoinedReqDto dto) {
		return userRepository.mCheckJoined(dto.getUniqueKey());
	}

	@Transactional
	public UserRespDto 회원가입(JoinReqDto dto) {
		// 비밀번호 해쉬
		String encPassword = encoder.encode(dto.getPassword());
		dto.encryptPassword(encPassword);
		User userEntity = userRepository.save(dto.toEntity());
		return new UserRespDto(userEntity);
	}

	@Transactional(readOnly = true)
	public int 아이디중복확인(CheckUsernameReqDto dto) {
		return userRepository.mCheckUsername(dto.getUsername());
	}

	@Transactional(readOnly = true)
	public FindIdRespDto 아이디찾기(String method, String data) {
		User userEntity;
		// 1. DB에서 아이디 조회
		if (method.equals("email")) {
			userEntity = userRepository.findByEmail(data);
		} else {
			userEntity = userRepository.findByPhone(data);
		}
		// 2. 아이디 조회 결과
		if (userEntity != null) {
			return new FindIdRespDto(userEntity);
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public int 비밀번호찾기(String method, String data, String username) {
		int result = 0;
		// DB에서 회원정보 조회
		if (method.equals("email")) {
			result = userRepository.mCheckUsernameAndEmail(username, data);
		} else {
			result = userRepository.mCheckUsernameAndPhone(username, data);
		}
		return result; // 있으면 1, 없으면 0
	}

	@Transactional
	public UserRespDto 비밀번호재설정(ResetPwReqDto dto) {
		User userEntity = userRepository.findByUsername(dto.getUsername());
		String encPassword = encoder.encode(dto.getNewPassword());
		userEntity.changePassword(encPassword); // 비밀번호 변경
		return new UserRespDto(userEntity);
	}

	@Transactional
	public void 비밀번호변경(Long userId, ChangePwReqDto dto) {
		User userEntity = userRepository.findById(userId).orElseThrow(() -> {
			throw new NoResultException("존재하지 않는 사용자");
		});

		// 현재 비밀번호 일치 여부 확인
		if (encoder.matches(dto.getCurPassword(), userEntity.getPassword())) {
			// 변경 성공
			String encPassword = encoder.encode(dto.getNewPassword());
			userEntity.changePassword(encPassword);
		} else {
			// 변경 실패
			throw CustomException.builder().code(401).message("인증되지 않은 사용자").response("현재 비밀번호가 일치하지 않습니다.")
					.httpStatue(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Transactional
	public UserRespDto 이메일변경(Long userId, ChangeEmailReqDto dto) {
		User userEntity = userRepository.findById(userId).orElseThrow(() -> {
			throw new NoResultException("존재하지 않는 사용자");
		});

		userEntity.changeEmail(dto.getEmail());
		return new UserRespDto(userEntity);
	}

	// 매장삭제 전 비밀번호 확인용
	@Transactional(readOnly = true)
	public boolean 비밀번호확인(Long userId, CheckPwReqDto dto) {
		User userEntity = userRepository.findById(userId).orElseThrow(() -> {
			throw new NoResultException("존재하지 않는 사용자");
		});
		// 비밀번호 일치 시 true 리턴
		return encoder.matches(dto.getPassword(), userEntity.getPassword());
	}

	@Transactional
	public WithdrawalRespDto 회원탈퇴(Long userId, CheckPwReqDto dto) {
		User userEntity = userRepository.findById(userId).orElseThrow(() -> {
			throw new NoResultException("존재하지 않는 사용자");
		});

		// 비밀번호 일치 여부 확인
		if (encoder.matches(dto.getPassword(), userEntity.getPassword())) {
			// 1. 나의 매장 ID 전체조회
			List<Long> storeIdList = storeRepository.mFindAllIdByUserId(userId);
			// 2. 나의 매장 전체삭제
			for (Long storeId : storeIdList) {
				storeService1.매장삭제(storeId, userId);
			}
			// 3.회원 계정삭제
			userRepository.delete(userEntity);
			return new WithdrawalRespDto(userEntity.getName(), storeIdList.size()); // 탈퇴 성공
		} else {
			// 탈퇴 실패 (비밀번호 불일치)
			throw CustomException.builder().code(401).message("인증되지 않은 사용자").response("비밀번호가 일치하지 않습니다.")
					.httpStatue(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
