package com.example.tablenow.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	// 가입 여부 조회
	@Query(value = "SELECT COUNT(*) FROM users WHERE uniqueKey = :uniqueKey", nativeQuery = true)
	public int mCheckJoined(@Param("uniqueKey") String uniqueKey);

	// 아이디 중복확인
	@Query(value = "SELECT COUNT(*) FROM users WHERE username = :username", nativeQuery = true)
	public int mCheckUsername(@Param("username") String username);

	// 해당 아이디&이메일을 가진 회원이 존재하는지
	@Query(value = "SELECT COUNT(*) FROM users WHERE username = :username AND email = :email", nativeQuery = true)
	public int mCheckUsernameAndEmail(@Param("username") String username, @Param("email") String email);

	// 해당 아이디&휴대폰번호를 가진 회원이 존재하는지
	@Query(value = "SELECT COUNT(*) FROM users WHERE username = :username AND phone = :phone", nativeQuery = true)
	public int mCheckUsernameAndPhone(@Param("username") String username, @Param("phone") String phone);

	// findBy규칙
	// SELECT * FROM users WHERE username = ?
	public User findByUsername(String username);

	// SELECT * FROM users WHERE email = ?
	public User findByEmail(String email);

	// SELECT * FROM users WHERE phone = ?
	public User findByPhone(String phone);
}
