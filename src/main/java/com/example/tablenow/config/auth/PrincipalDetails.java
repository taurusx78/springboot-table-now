package com.example.tablenow.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.tablenow.domain.user.User;

import lombok.Getter;

@Getter
public class PrincipalDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private User user;

	public PrincipalDetails(User user) {
		this.user = user;
	}

	// 사용자 권환 리턴 (한 사용자는 여러 권한을 가질 수 있음)
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(() -> {
			return "ROLE_MANAGER";
		});
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	// 계정 만료 여부
	@Override
	public boolean isAccountNonExpired() {
		return true; // 만료되지 않음
	}

	// 계정 잠김 여부
	@Override
	public boolean isAccountNonLocked() {
		return true; // 잠기지 않음
	}

	// 비밀번호 만료 여부
	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 만료되지 않음
	}

	// 계정 활성화 여부
	@Override
	public boolean isEnabled() {
		return true; // 활성화되어 있음
	}
}
