// package com.example.tablenow.domain.store;

// import static org.assertj.core.api.Assertions.assertThat;

// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.test.context.ActiveProfiles;

// import com.example.tablenow.domain.user.User;
// import com.example.tablenow.domain.user.UserRepository;

// @ActiveProfiles("dev") // dev 모드에서만 동작하도록 설정
// @DataJpaTest // DB 관련 컴포넌트만 메모리에 로딩 (예. Repository)
// public class StoreRepositoryTest {

//     @Autowired
//     private StoreRepository storeRepository;

//     @Autowired
//     private UserRepository userRepository;

//     Store storePS; // Store 엔티티
//     User userPS; // 매장 등록한 User 엔티티

//     @BeforeEach // 각 테스트 시작 전마다 실행
//     public void 데이터준비() {
//         String name = "매장명1";
//         String category = "카테고리1";
//         String phone = "02-1111-1111";
//         String address = "도로명주소1";
//         String jibunAddress = "지번주소1";
//         double latitude = 37.1;
//         double longitude = 127.1;
//         String description = "매장소개1";
//         String website = "www.mystore1.com";

//         userPS = userRepository.save(User.builder().username("tablenow").password("tablenow1!").name("김지은")
//                 .phone("01011111111").uniqueKey("uniqueKey1").email("tablenow@naver.com").build());

//         storePS = storeRepository.save(
//                 Store.builder().name(name).category(category).phone(phone).address(address).jibunAddress(jibunAddress)
//                         .latitude(latitude).longitude(longitude).description(description).website(website).user(userPS)
//                         .build());
//     } // 각 테스트가 끝나기 전까지 트랜잭션 유지

//     // 등록여부조회
//     @Test
//     public void mCheckExist() {
//         // given
//         String name = "매장명1";
//         String category = "카테고리1";
//         String phone = "02-1111-1111";
//         String address = "도로명주소1";

//         // when
//         int result = storeRepository.mCheckExist(name, category, phone, address);

//         // then
//         assertThat(result).isEqualTo(1);
//     }

//     // 매장등록 - Store 등록 테스트
//     @Test
//     public void save() {
//         // given
//         String name = "매장명2";
//         String category = "카테고리2";
//         String phone = "02-2222-2222";
//         String address = "도로명주소2";
//         String jibunAddress = "지번주소2";
//         double latitude = 37.2;
//         double longitude = 127.2;
//         String description = "매장소개2";
//         String website = "www.mystore2.com";

//         Store store = Store.builder().name(name).category(category).phone(phone).address(address)
//                 .jibunAddress(jibunAddress).latitude(latitude).longitude(longitude).description(description)
//                 .website(website).user(userPS).build();

//         // when
//         Store storePS = storeRepository.save(store);

//         // then
//         assertThat(storePS.getName()).isEqualTo(name);
//         assertThat(storePS.getCategory()).isEqualTo(category);
//         assertThat(storePS.getPhone()).isEqualTo(phone);
//         assertThat(storePS.getAddress()).isEqualTo(address);
//         assertThat(storePS.getJibunAddress()).isEqualTo(jibunAddress);
//         assertThat(storePS.getLatitude()).isEqualTo(latitude);
//         assertThat(storePS.getLongitude()).isEqualTo(longitude);
//         assertThat(storePS.getDescription()).isEqualTo(description);
//         assertThat(storePS.getWebsite()).isEqualTo(website);
//         assertThat(storePS.getUser()).isEqualTo(userPS);
//     }

//     // 사용자의 매장이 맞는지 확인
//     @Test
//     public void mCheckStoreManager() {
//         // given
//         Long storeId = storePS.getId();
//         Long userId = userPS.getId();

//         // when
//         int result1 = storeRepository.mCheckStoreManager(storeId, userId);
//         int result2 = storeRepository.mCheckStoreManager(storeId, userId + 1L);

//         // then
//         assertThat(result1).isEqualTo(1);
//         assertThat(result2).isEqualTo(0);
//     }

//     // Store 데이터 영속화
//     @Test
//     public void findById() {
//         // given
//         Long storeId = storePS.getId();

//         // when
//         Optional<Store> storeOP1 = storeRepository.findById(storeId);
//         Optional<Store> storeOP2 = storeRepository.findById(storeId + 1L);

//         // then
//         assertThat(storeOP1.get().getId()).isEqualTo(storeId);
//         assertThat(storeOP2).isEmpty();
//     }

//     // 나의 매장 ID 전체조회
//     @Test
//     public void mFindAllIdByUserId() {
//         // given
//         Long userId = userPS.getId();

//         // when
//         List<Long> storeIdList = storeRepository.mFindAllIdByUserId(userId);

//         // then
//         assertThat(storeIdList.size()).isEqualTo(1);
//         assertThat(storeIdList.get(0)).isEqualTo(storePS.getId());
//     }

//     // 매장명 조회
//     @Test
//     public void mFindNameByStoreId() {
//         // given
//         Long storeId = storePS.getId();

//         // when
//         String storeName = storeRepository.mFindNameByStoreId(storeId);

//         // then
//         assertThat(storeName).isEqualTo(storePS.getName());
//     }
// }
