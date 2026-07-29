## 요구사항

### 기본

- [x] 1차: Java·Gradle 프로젝트와 User, Channel, Message 도메인 모델 구현
- [x] 1차: Service 인터페이스와 JCF 기반 CRUD 구현
- [x] 1차: JavaApplication에서 도메인별 CRUD 검증
- [x] 2차: File IO와 객체 직렬화 기반 저장 구현
- [x] 2차: JCF·File 저장 로직을 Repository로 분리
- [x] 2차: Basic Service에 Repository 생성자 주입

### 심화

- [x] Message 생성 시 연관 User와 Channel 존재 검증
- [x] JCF·File Repository를 교체해도 Service 비즈니스 로직 유지
- [x] JCF·File 구성의 CRUD와 File 데이터 재조회 검증

## 주요 변경사항

- feat: Discodeit 도메인 모델 구현
- feat: CRUD 서비스 인터페이스 정의
- feat: User와 Channel JCF 서비스 구현
- feat: Message 연관 관계 검증 및 서비스 구성
- feat: JavaApplication 실행 흐름 추가
- feat: File 기반 서비스 구현
- feat: Basic 서비스와 CRUD Repository 계약 추가
- refactor: 저장 로직을 Repository 계층으로 분리
- refactor: 공통 ObjectStore를 도입 Repository 중복 저장 로직 제거

## 테스트

- [x] `./gradlew clean test`
- [x] JCF 구성 실행
- [x] File 구성 실행 및 재생성 후 저장 데이터 조회

## 멘토에게

- 셀프 코드 리뷰를 통해 질문 이어가겠습니다.
-
