# Discodeit Sprint Mission Requirements

## 원문 요구사항

### 목표

Git과 GitHub을 통해 프로젝트를 관리할 수 있다.
채팅 서비스의 도메인 모델을 설계하고, Java로 구현할 수 있다.
인터페이스를 설계하고 구현체를 구현할 수 있다.
싱글톤 패턴을 구현할 수 있다.
Java Collections Framework에 데이터를 생성/수정/삭제할 수 있다.
Stream API를 통해 JCF의 데이터를 조회할 수 있다.
[심화] 모듈 간 의존 관계를 이해하고 팩토리 패턴을 활용해 의존성을 관리할 수 있다.

### 프로젝트 마일스톤

프로젝트 초기화 (Java, Gradle)
도메인 모델 구현
서비스 인터페이스 설계 및 구현체 구현
각 도메인 모델별 CRUD
JCFx메모리 기반
의존성 주입

### 요구사항

#### 기본 요구사항

##### 프로젝트 초기화

[ ] IntelliJ를 통해 다음의 조건으로 Java 프로젝트를 생성합니다.
[ ]  IntelliJ에서 제공하는 프로젝트 템플릿 중 Java를 선택합니다.

[ ]  프로젝트의 경로는 스프린트 미션 리포지토리의 경로와 같게 설정합니다.

예를 들어 스프린트 미션 리포지토리의 경로가 /some/path/1-sprint-mission 이라면:

Name은 1-sprint-mission
Location은 /some/path
으로 설정합니다.

[ ]  Create Git Repository 옵션은 체크하지 않습니다.

[ ]  Build system은 Gradle을 사용합니다. Gradle DSL은 Groovy를 사용합니다.

[ ]  JDK 17을 선택합니다.

[ ]  GroupId는 com.sprint.mission로 설정합니다.

[ ]  ArtifactId는 수정하지 않습니다.

[ ]  .gitignore에 IntelliJ와 관련된 파일이 형상관리 되지 않도록 .idea디렉토리를 추가합니다.

...
.idea
...

##### 도메인 모델링

[ ] 디스코드 서비스를 활용해보면서 각 도메인 모델에 필요한 정보를 도출하고, Java Class로 구현하세요.
[ ] 패키지명: com.sprint.mission.discodeit.entity
[ ] 도메인 모델 정의
[ ] 공통
[ ] id: 객체를 식별하기 위한 id로 UUID 타입으로 선언합니다.
[ ] createdAt, updatedAt: 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드로 Long 타입으로 선언합니다.
[ ] User
[ ] Channel
[ ] Message
[ ] 생성자
[ ] id는 생성자에서 초기화하세요.
[ ] createdAt는 생성자에서 초기화하세요.
[ ] id, createdAt, updatedAt을 제외한 필드는 생성자의 파라미터를 통해 초기화하세요.
[ ] 메소드
[ ] 각 필드를 반환하는 Getter 함수를 정의하세요.
[ ] 필드를 수정하는 update 함수를 정의하세요.

##### 서비스 설계 및 구현

[ ] 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언하세요.
[ ] 인터페이스 패키지명: com.sprint.mission.discodeit.service
[ ] 인터페이스 네이밍 규칙: [도메인 모델 이름]Service
[ ] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
[ ] 클래스 패키지명: com.sprint.mission.discodeit.service.jcf
[ ] 클래스 네이밍 규칙: JCF[인터페이스 이름]
[ ] Java Collections Framework를 활용하여 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화하세요.
[ ] data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.

##### 메인 클래스 구현

[ ] 메인 메소드가 선언된 JavaApplication 클래스를 선언하고, 도메인 별 서비스 구현체를 테스트해보세요.
[ ] 등록
[ ] 조회(단건, 다건)
[ ] 수정
[ ] 수정된 데이터 조회
[ ] 삭제
[ ] 조회를 통해 삭제되었는지 확인

#### 심화 요구 사항

##### 서비스 간 의존성 주입

[ ] 도메인 모델 간 관계를 고려해서 검증하는 로직을 추가하고, 테스트해보세요.
힌트: Message를 생성할 때 연관된 도메인 모델 데이터 확인하기

## 완료 조건

### 프로젝트 초기화

- [ ] `REQ-001` 기존 스프린트 미션 Git 저장소가 프로젝트 루트이며 별도의 Git 저장소를 중첩 생성하지 않았다.
- [ ] `REQ-002` Gradle Groovy DSL과 Java 17 toolchain으로 프로젝트를 빌드할 수 있다.
- [ ] `REQ-003` Gradle Group은 `com.sprint.mission`이고 프로젝트 이름은 저장소 이름인 `14-sprint-mission`이다.
- [ ] `REQ-004` `.gitignore`가 `.idea/`와 Gradle 빌드 산출물을 제외한다.

### 도메인 모델

- [ ] `REQ-005` `com.sprint.mission.discodeit.entity`에 `User`, `Channel`, `Message`가 존재한다.
- [ ] `REQ-006` 모든 도메인 모델의 `id`는 `UUID`, `createdAt`과 `updatedAt`은 `Long` 타입이다.
- [ ] `REQ-007` 생성자가 `id`와 `createdAt`을 내부에서 초기화하고, `updatedAt`을 제외한 나머지 도메인 필드를 파라미터로 받는다.
- [ ] `REQ-008` 생성 시점의 `updatedAt` 초기값을 의도적으로 정하고 그 의미를 설명할 수 있다.
- [ ] `REQ-009` 각 필드의 Getter와 변경 가능한 필드를 수정하는 update 메서드가 있다.
- [ ] `REQ-010` update가 실제 변경이 발생한 시점에 `updatedAt`을 유닉스 타임스탬프로 갱신한다.
- [ ] `REQ-011` `User`, `Channel`, `Message`의 필드가 채팅 도메인에서 맡는 역할과 모델 간 관계를 설명할 수 있다.

### 서비스 계약

- [ ] `REQ-012` `com.sprint.mission.discodeit.service`에 `UserService`, `ChannelService`, `MessageService`가 존재한다.
- [ ] `REQ-013` 각 서비스 인터페이스가 생성, 단건 조회, 전체 조회, 수정, 삭제 기능을 선언한다.
- [ ] `REQ-014` 각 CRUD 메서드의 파라미터와 반환 타입이 성공·실패와 데이터 부재를 어떻게 표현하는지 일관된 계약을 가진다.

### JCF 기반 구현

- [ ] `REQ-015` `com.sprint.mission.discodeit.service.jcf`에 `JCFUserService`, `JCFChannelService`, `JCFMessageService`가 존재하고 대응하는 서비스 인터페이스를 구현한다.
- [ ] `REQ-016` 각 구현체에 JCF 기반 `data` 필드가 `final`로 선언되고 생성자에서 초기화된다.
- [ ] `REQ-017` 생성 시 UUID 식별자를 키로 데이터를 저장하고, 중복 또는 잘못된 입력을 정한 정책에 따라 처리한다.
- [ ] `REQ-018` 단건 조회가 UUID로 데이터를 찾고, 데이터 부재를 서비스 계약에 맞게 처리한다.
- [ ] `REQ-019` 전체 조회가 Stream API를 사용해 저장된 데이터를 조회하고 내부 컬렉션이 외부에서 임의로 변경되지 않게 반환한다.
- [ ] `REQ-020` 수정이 기존 객체를 찾아 도메인의 update 메서드를 통해 상태와 `updatedAt`을 변경한다.
- [ ] `REQ-021` 삭제가 대상 데이터를 제거하고 이후 조회에서 삭제 여부를 확인할 수 있다.

### 실행 검증

- [ ] `REQ-022` `com.sprint.mission.discodeit.JavaApplication`에 `main` 메서드가 존재한다.
- [ ] `REQ-023` 각 도메인 서비스에 대해 등록, 단건 조회, 다건 조회를 실행한다.
- [ ] `REQ-024` 각 도메인 서비스에 대해 수정, 수정 결과 조회를 실행한다.
- [ ] `REQ-025` 각 도메인 서비스에 대해 삭제, 삭제 후 조회를 실행해 제거 여부를 확인한다.

### 싱글톤

- [ ] `REQ-026` 동일 서비스 구현체를 공유해야 하는 범위를 정하고 싱글톤 패턴을 직접 구현해 동작을 확인한다.

### 심화: 관계 검증과 의존성 관리

- [ ] `REQ-027` `Message` 생성 시 연관된 `User`와 `Channel`이 실제 저장소에 존재하는지 검증한다.
- [ ] `REQ-028` 관계 검증에 필요한 서비스를 생성자 주입으로 전달하여 구현체 내부에서 직접 생성하지 않는다.
- [ ] `REQ-029` 존재하지 않는 연관 데이터로 Message를 생성할 때 실패 원인과 처리 방식이 명확하다.
- [ ] `REQ-030` 팩토리가 서비스 구현체 생성과 의존성 연결을 한곳에서 담당하며 호출 코드는 구체 구현체 생성 순서를 알 필요가 없다.

## 제약사항

- Java 17을 사용한다.
- Gradle과 Groovy DSL을 사용한다.
- 프로젝트 내부에서 `git init`을 다시 실행하지 않는다.
- 도메인 패키지는 `com.sprint.mission.discodeit.entity`를 사용한다.
- 서비스 인터페이스 패키지는 `com.sprint.mission.discodeit.service`를 사용한다.
- JCF 구현체 패키지는 `com.sprint.mission.discodeit.service.jcf`를 사용한다.
- 서비스 인터페이스는 `[도메인 모델 이름]Service`, 구현체는 `JCF[인터페이스 이름]` 규칙을 따른다.
- 영속 데이터베이스 대신 Java Collections Framework를 사용한 메모리 저장소로 구현한다.
- Lombok이 빌드에 포함되어 있어도 생성자와 Getter를 직접 설계·구현하는 과제 목표를 우회하는 용도로 사용하지 않는다.

## 열린 질문

- `User`, `Channel`, `Message` 각각에 필요한 세부 필드가 원문에 명시되어 있지 않다. Discord 관찰 결과를 근거로 학습자가 정해야 한다.
- `User`와 `Channel`, `Message`의 관계를 객체 참조로 보관할지 UUID로 보관할지 명시되어 있지 않다.
- 채널의 공개/비공개 유형, 참여자 목록, 메시지 작성자와 채널 식별자 등 구체 모델 범위가 명시되어 있지 않다.
- `updatedAt`의 생성 직후 값이 `null`인지 `createdAt`과 같은 값인지 명시되어 있지 않다.
- update 메서드가 필드별 파라미터, 변경 요청 객체, 또는 새 도메인 객체 중 무엇을 받을지 명시되어 있지 않다.
- CRUD 메서드의 정확한 파라미터와 반환 타입, 데이터 부재 및 실패 처리 방식이 명시되어 있지 않다.
- 전체 조회의 정렬 기준과 반환 컬렉션 타입이 명시되어 있지 않다.
- 삭제 시 연관 Message를 함께 삭제할지, 삭제를 거부할지, 고아 데이터를 허용할지 명시되어 있지 않다.
- 싱글톤을 적용할 정확한 대상과 팩토리의 이름·API가 명시되어 있지 않다.
- 심화 요구사항을 기본 완료에 필수로 포함할지 별도 가산 항목으로 평가할지 명시되어 있지 않다.

## 범위 제외

- Spring Framework 또는 Spring Boot
- 관계형 데이터베이스나 파일 기반 영속화
- HTTP, WebSocket, REST API
- 실제 Discord API 연동
- 사용자 인증·인가
- 콘솔 입출력 형식의 세부 정의
- 동시성 및 멀티스레드 안전성

## 추가 원문 요구사항

### 기본 요구사항

#### File IO를 통한 데이터 영속화

[ ] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.

[ ] 클래스 패키지명: com.sprint.mission.discodeit.service.file

[ ] 클래스 네이밍 규칙: File[인터페이스 이름]

[ ] JCF 대신 FileIO와 객체 직렬화를 활용해 메소드를 구현하세요.

##### 객체 직렬화/역직렬화 가이드

[ ] Application에서 서비스 구현체를 File*Service로 바꾸어 테스트해보세요.

#### 서비스 구현체 분석

[ ] JCF*Service 구현체와 File*Service 구현체를 비교하여 공통점과 차이점을 발견해보세요.

[ ] "비즈니스 로직"과 관련된 코드를 식별해보세요.

[ ] "저장 로직"과 관련된 코드를 식별해보세요.

#### 레포지토리 설계 및 구현

[ ] "저장 로직"과 관련된 기능을 도메인 모델 별 인터페이스로 선언하세요.

[ ] 인터페이스 패키지명: com.sprint.mission.discodeit.repository

[ ] 인터페이스 네이밍 규칙: [도메인 모델 이름]Repository

[ ] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.

[ ] 클래스 패키지명: com.sprint.mission.discodeit.repository.jcf

[ ] 클래스 네이밍 규칙: JCF[인터페이스 이름]

[ ] 기존에 구현한 JCF*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.

[ ] 다음의 조건을 만족하는 레포지토리 인터페이스의 구현체를 작성하세요.

[ ] 클래스 패키지명: com.sprint.mission.discodeit.repository.file

[ ] 클래스 네이밍 규칙: File[인터페이스 이름]

[ ] 기존에 구현한 File*Service 구현체의 "저장 로직"과 관련된 코드를 참고하여 구현하세요.

### 심화 요구 사항

#### 관심사 분리를 통한 레이어 간 의존성 주입

[ ] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.

[ ] 클래스 패키지명: com.sprint.mission.discodeit.service.basic

[ ] 클래스 네이밍 규칙: Basic[인터페이스 이름]

[ ] 기존에 구현한 서비스 구현체의 "비즈니스 로직"과 관련된 코드를 참고하여 구현하세요.

[ ] 필요한 Repository 인터페이스를 필드로 선언하고 생성자를 통해 초기화하세요.

[ ] "저장 로직"은 Repository 인터페이스 필드를 활용하세요. (직접 구현하지 마세요.)

[ ] Basic*Service 구현체를 활용하여 테스트해보세요.

##### 코드 템플릿

```java
public class JavaApplication {
    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        // 서비스 초기화
        // TODO Basic*Service 구현체를 초기화하세요.
        UserService userService;
        ChannelService channelService;
        MessageService messageService;

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }
}
```

[ ] JCF*Repository 구현체를 활용하여 테스트해보세요.

[ ] File*Repository 구현체를 활용하여 테스트해보세요.

[ ] 이전에 작성했던 코드(JCF*Service 또는 File*Service)와 비교해 어떤 차이가 있는지 정리해보세요.

## 추가 완료 조건

### File IO 기반 서비스 구현

- [ ] `REQ-031` `com.sprint.mission.discodeit.service.file`에 `FileUserService`, `FileChannelService`, `FileMessageService`가 존재하고 각각 대응하는 서비스 인터페이스를 구현한다.
- [ ] `REQ-032` 파일에 저장되는 `User`, `Channel`, `Message`와 직렬화 대상 객체 그래프가 Java 객체 직렬화 및 역직렬화가 가능한 계약을 갖는다.
- [ ] `REQ-033` 각 `File*Service`의 생성, 단건 조회, 전체 조회, 수정, 삭제 메서드는 JCF 기반 필드를 영속 저장소로 사용하지 않고 File IO와 객체 직렬화를 통해 데이터를 저장하고 불러온다.
- [ ] `REQ-034` 파일이 없거나 비어 있거나 손상된 경우와 입출력 또는 직렬화에 실패한 경우의 처리 정책이 명확하며, 서비스 계약에 맞는 예외나 결과로 호출자에게 전달된다.
- [ ] `REQ-035` `File*Service` 인스턴스를 새로 생성한 뒤에도 앞서 파일에 기록한 데이터를 조회할 수 있어 파일 기반 영속화를 확인할 수 있다.
- [ ] `REQ-036` `JavaApplication`에서 도메인별 서비스 구현체를 `File*Service`로 구성하고 생성, 조회, 수정, 삭제와 재실행 후 조회를 테스트한다.

### 서비스 구현체 분석

- [ ] `REQ-037` `JCF*Service`와 `File*Service`가 공유하는 서비스 계약과 비즈니스 규칙, 서로 다른 저장 매체와 실패 가능성을 실제 코드 기준으로 비교해 정리한다.
- [ ] `REQ-038` 입력 검증, 연관 도메인 존재 확인, 도메인 객체 생성 및 변경처럼 저장 매체와 무관한 비즈니스 로직을 실제 코드에서 식별한다.
- [ ] `REQ-039` 데이터 보관, 식별자 기반 조회, 전체 데이터 로딩, 갱신 결과 반영, 삭제처럼 저장 매체에 의존하는 저장 로직을 실제 코드에서 식별한다.

### Repository 설계 및 구현

- [ ] `REQ-040` `com.sprint.mission.discodeit.repository`에 `UserRepository`, `ChannelRepository`, `MessageRepository`가 존재한다.
- [ ] `REQ-041` 각 Repository 인터페이스가 서비스에 필요한 생성 결과 저장, 단건 조회, 전체 조회, 수정 결과 저장, 삭제 기능을 저장 매체와 무관한 계약으로 선언한다.
- [ ] `REQ-042` Repository의 데이터 부재, 중복 식별자, 수정 및 삭제 실패에 대한 반환값 또는 예외 정책이 서비스 계약과 일관된다.
- [ ] `REQ-043` `com.sprint.mission.discodeit.repository.jcf`에 `JCFUserRepository`, `JCFChannelRepository`, `JCFMessageRepository`가 존재하고 대응하는 Repository 인터페이스를 구현한다.
- [ ] `REQ-044` 각 `JCF*Repository`는 기존 `JCF*Service`에서 분리한 JCF 기반 저장 로직으로 생성 결과 저장, 조회, 수정 결과 반영, 삭제를 수행한다.
- [ ] `REQ-045` `com.sprint.mission.discodeit.repository.file`에 `FileUserRepository`, `FileChannelRepository`, `FileMessageRepository`가 존재하고 대응하는 Repository 인터페이스를 구현한다.
- [ ] `REQ-046` 각 `File*Repository`는 기존 `File*Service`에서 분리한 File IO와 객체 직렬화 기반 저장 로직으로 생성 결과 저장, 조회, 수정 결과 반영, 삭제를 수행한다.
- [ ] `REQ-047` `File*Repository` 인스턴스를 새로 생성해도 이전 인스턴스가 기록한 데이터를 조회할 수 있고, 서로 다른 도메인의 저장 파일이나 데이터가 충돌하지 않는다.

### Basic 서비스와 의존성 주입

- [ ] `REQ-048` `com.sprint.mission.discodeit.service.basic`에 `BasicUserService`, `BasicChannelService`, `BasicMessageService`가 존재하고 각각 대응하는 서비스 인터페이스를 구현한다.
- [ ] `REQ-049` 각 `Basic*Service`는 필요한 Repository 인터페이스를 `final` 필드로 선언하고 생성자 주입으로 초기화하며, 구체 Repository 구현체를 내부에서 직접 생성하지 않는다.
- [ ] `REQ-050` 각 `Basic*Service`는 입력 검증, 도메인 객체 생성과 변경, 연관 도메인 검증 등의 비즈니스 로직을 담당하고 저장 및 조회는 Repository 인터페이스에 위임한다.
- [ ] `REQ-051` `BasicMessageService`가 Message 생성에 필요한 User와 Channel의 존재 여부를 Repository 인터페이스를 통해 검증하고, 존재하지 않을 때 명확한 실패 정책을 적용한다.

### 구성 및 실행 검증

- [ ] `REQ-052` `JavaApplication`에서 `JCF*Repository`를 생성자 주입한 `Basic*Service` 조합으로 User, Channel, Message 생성과 주요 CRUD 동작을 테스트한다.
- [ ] `REQ-053` `JavaApplication`에서 `File*Repository`를 생성자 주입한 `Basic*Service` 조합으로 같은 동작을 테스트하고, Repository와 서비스를 다시 생성한 뒤에도 저장 데이터가 조회되는지 확인한다.
- [ ] `REQ-054` `JCF*Repository`와 `File*Repository`를 교체할 때 서비스의 비즈니스 로직을 수정하지 않고 애플리케이션 구성 코드만 변경해 실행할 수 있다.
- [ ] `REQ-055` 기존 `JCF*Service` 및 `File*Service`와 `Basic*Service`를 비교하여 비즈니스 로직과 저장 로직의 위치, 구체 저장 기술에 대한 결합도, 테스트 및 교체 용이성의 차이를 정리한다.

## 추가 요구사항 적용 메모

- 추가 원문 요구사항은 기존 `범위 제외`의 `관계형 데이터베이스나 파일 기반 영속화` 중 파일 기반 영속화 범위를 후속 단계에서 확장한다. 관계형 데이터베이스는 계속 범위에서 제외한다.
- 기존 `REQ-001`부터 `REQ-030`까지의 완료 조건은 유지하며, 추가 완료 조건은 `REQ-031`부터 이어서 관리한다.
- `File*Service`는 Repository 분리 전 File IO 구현을 학습하기 위한 중간 결과물이고, `Basic*Service`는 저장 로직을 Repository로 분리한 최종 서비스 구조로 취급한다.

## 추가 열린 질문

- 제공된 코드 템플릿은 서비스 메서드명을 `create`로 사용하지만 현재 서비스 인터페이스는 `createUser`, `createChannel`, `createMessage`를 사용한다. 기존 인터페이스명을 유지할지 템플릿에 맞춰 공통 메서드명으로 변경할지 명시되어 있지 않다.
- 제공된 코드 템플릿의 Message 생성 인자는 `content`, `channelId`, `authorId`이지만 현재 `MessageService.createMessage`와 `Message`는 추가로 `receiverId`를 요구한다. 수신자 모델을 유지할지 템플릿에 맞춰 제거할지 명시되어 있지 않다.
- 도메인별 파일의 정확한 경로와 파일명, 하나의 파일에 단일 객체 또는 전체 컬렉션 중 무엇을 직렬화할지는 명시되어 있지 않다.
- 파일 저장 시 임시 파일과 원자적 교체를 사용할지, 프로세스 중단으로 생긴 부분 기록을 어떻게 복구할지는 명시되어 있지 않다.
- Repository의 정확한 메서드명, 파라미터, 반환 타입과 `Optional` 사용 여부는 명시되어 있지 않다.
