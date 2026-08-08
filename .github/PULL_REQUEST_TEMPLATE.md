## 요구사항

### Spring 프로젝트 초기화

- [x] Java 17, Gradle Groovy, Spring Boot 3.4.0 적용
- [x] Group `com.sprint.mission`, Artifact/Name `discodeit`, Jar 구성
- [x] Spring Web, Lombok, DevTools 의존성 적용
- [x] `application.yml` 사용

### Bean 선언과 Lombok

- [x] `File*Repository`를 Repository 인터페이스 Bean으로 등록
- [x] `*ServiceImpl`을 Service 인터페이스 Bean으로 등록
- [x] 도메인 Getter를 Lombok `@Getter`로 생성
- [x] Service 구현체 생성자를 Lombok `@RequiredArgsConstructor`로 생성
- [x] `DiscodeitApplication`에서 Spring Context로 Service Bean 조회 및 생성 흐름 실행

### 비즈니스 로직

- [x] user, channel, message 등 도메인 우선 패키지 구조 적용
- [x] 공통 DTO Mapper를 도메인별 Mapper로 분리
- [x] UUID 관계와 DTO 서비스 경계 적용
- [x] 사용자·채널·메시지·읽음 상태·접속 상태·바이너리 콘텐츠 구현
- [x] 중복 필드·중복 연관·중복 요청 값과 엔티티 부재 예외 구분
- [x] 채널/메시지/프로필 삭제 시 연관 데이터 정리
- [x] JCF와 File Repository가 동일한 포트를 구현

## JavaApplication과 DiscodeitApplication의 차이

- **IoC Container**: JavaApplication은 구현체를 직접 생성하고 연결하지만, DiscodeitApplication은 `ApplicationContext`가 객체 생성과 생명주기를 관리합니다.
- **Dependency Injection**: JavaApplication은 생성자 인자를 호출 코드가 직접 전달합니다. Spring에서는 Service 구현체의 생성자를 통해 Container가 Repository Bean을 주입합니다.
- **Bean**: `@Bean`으로 선언된 File Repository와 `@Service`로 탐색된 `*ServiceImpl`은 Container가 관리하는 Bean입니다. 실행 코드는 `context.getBean(UserControllerService.class)`처럼 구현체가 아닌 Controller 계약 인터페이스로 조회합니다.

결과적으로 실행 코드는 구체 구현체 생성 순서를 알 필요가 없고, 저장 전략 변경은
Service가 아니라 Repository 조립 설정에 한정됩니다.

## 테스트

- [x] `./gradlew clean test`
- [x] `DiscodeitApplication.main` 실행 로그 확인

## 멘토에게

- File Repository를 Bean으로 고정하면서도 JCF 구현은 포트 동등성 테스트용으로 유지했습니다.
