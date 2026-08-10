요구사항
---
- [x] Spring 프로젝트 초기화
- [x] Bean 선언 및 테스트
- [x] Spring 3 - Layered 구현
- [x] 도메인 모델 구현 완료

## JavaApplication과 DiscodeitApplication의 차이

- **IoC Container**: JavaApplication은 구현체를 직접 생성하고 연결하지만, DiscodeitApplication은 `ApplicationContext`가 객체 생성과 생명주기를 관리합니다.
- **Dependency Injection**: JavaApplication은 생성자 인자를 호출 코드가 직접 전달합니다. Spring에서는 Service 구현체의 생성자를 통해 Container가 Repository Bean을 주입합니다.
- **Bean**: `@Bean`으로 선언된 File Repository와 `@Service`로 탐색된 `*ServiceImpl`은 Container가 관리하는 Bean입니다. 실행 코드는 `context.getBean(UserControllerService.class)`처럼 구현체가 아닌 Controller 계약 인터페이스로 조회합니다.
