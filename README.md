# TABLE NOW (서버)
매장의 실시간 영업정보를 제공하는 서비스의 서버 프로그램이다.
<br><br>

## 설명
Spring Boot를 이용한 서버 프로그램으로 매장 관리자의 요청에 따라 정보를 조회/등록/수정/삭제하고, 고객의 요청에 따라 매장을 조회하는 역할을 수행한다.<br>
1. 서버는 클라이언트와 REST API 방식으로 JSON 데이터를 주고 받으며 통신한다.
2. 세션을 통한 인증의 문제점을 보완하기 위해, JWT를 통해 사용자 인증을 진행한다.
3. 사용자 입력값에 대해 유효성 검사를 진행하고, 해당 기능은 AOP를 적용해 코드의 중복을 줄였다.
4. Jnuit을 이용해 테스트를 진행함으로써 유지보수가 쉽도록 구성하였다. (코드 작성 미완)
<br>

## 기술스택
- Spring Boot
- JPA
- JWT
- AOP (Validation)
- MySQL
- Junit
<br>

## Open API
- 네이버 SENS (SMS 전송)
  - [네이버 SENS API 소개](https://www.ncloud.com/product/applicationService/sens)
  - [네이버 SENS API 가이드](https://api.ncloud-docs.com/docs/ai-application-service-sens-smsv2)
  <br>
- 네이버 검색 API (블로그)
  - [네이버 검색 API 소개](https://developers.naver.com/products/service-api/search/search.md)
<br>

## 추가할 기능
- (예정) 매장 일 조회수, 즐겨찾기 고객수, 정보수정제안 수 제공
  - 매장의 유의미한 통계 정보 제공
  <br>
- (보류) 고객 리뷰
  - 고객 계정 생성 필요함
<br>
