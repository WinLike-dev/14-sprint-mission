# 003 추상 팩토리 전환 리뷰

- 리뷰 일자: 2026-07-26
- 리뷰 범위: 현재 작업 트리의 `factory`, `JavaApplication`과 서비스·Repository 협력 구조
- 리뷰 기준 커밋: `693afa5`
- 작업 트리: 팩토리 전환 관련 수정 4개, 신규 파일 1개와 별도 `Channel` 수정 1개가 존재
- 전체 판정: 수정 필요

## 검증

- `./gradlew clean test`: 실패
  - `compileJava`에서 `ServiceFactory.createFileFactory`, `createJcFactory`,
    `getUserService`, `getChannelService`, `getMessageService`를 찾지 못해 8개 오류 발생
- 테스트 소스: `.gitkeep` 외 테스트 없음
- `git diff --check`: 실패
  - `FileServiceFactory.java`, `ServiceFactory.java`의 CRLF 줄바꿈이 추가 줄마다
    후행 공백으로 감지됨

## 요구사항 검토

이번 전환이 기존 요구사항에 만든 회귀를 놓치지 않도록 `REQ-001`부터 `REQ-055`까지
현재 소스 기준으로 확인했다.

| 요구사항 | 상태 | 직접 확인한 근거 |
| --- | --- | --- |
| `REQ-001` | 충족 | 저장소 루트의 `.git` 하나와 `02` 내부에 중첩 저장소가 없음을 확인했다. |
| `REQ-002` | 미충족 | `build.gradle`은 Java 17이지만 현재 `compileJava`가 실패한다. |
| `REQ-003` | 충족 | `build.gradle#group`, `settings.gradle#rootProject.name`이 요구값과 같다. |
| `REQ-004` | 충족 | 저장소 루트 `.gitignore`가 IDE와 Gradle 산출물을 제외한다. |
| `REQ-005` | 충족 | `entity` 패키지에 세 도메인 클래스가 존재한다. |
| `REQ-006` | 충족 | 세 엔티티의 식별자·시간 필드 타입이 요구와 같다. |
| `REQ-007` | 충족 | 세 엔티티 생성자가 식별자와 생성 시각을 내부 초기화한다. |
| `REQ-008` | 충족 | 세 엔티티가 생성 시 `updatedAt`을 `null`로 명시한다. |
| `REQ-009` | 충족 | Lombok Getter와 각 엔티티의 `update`가 존재한다. |
| `REQ-010` | 충족 | 각 `update`가 `updatedAt`을 현재 시각으로 변경한다. |
| `REQ-011` | 충족 | 필드와 UUID 관계가 세 엔티티에 표현돼 있다. |
| `REQ-012` | 충족 | `service` 패키지에 세 서비스 인터페이스가 존재한다. |
| `REQ-013` | 충족 | 세 서비스 계약이 CRUD를 모두 선언한다. |
| `REQ-014` | 충족 | 서비스와 현재 Store가 데이터 부재를 `IllegalStateException`으로 일관되게 전달한다. |
| `REQ-015` | 미충족 | 현재 소스에 `service.jcf.JCF*Service`가 없다. |
| `REQ-016` | 미충족 | 대상 `JCF*Service` 자체가 없다. |
| `REQ-017` | 미충족 | 대상 `JCF*Service` 자체가 없다. |
| `REQ-018` | 미충족 | 대상 `JCF*Service` 자체가 없다. |
| `REQ-019` | 미충족 | 대상 `JCF*Service` 자체가 없다. |
| `REQ-020` | 미충족 | 대상 `JCF*Service` 자체가 없다. |
| `REQ-021` | 미충족 | 대상 `JCF*Service` 자체가 없다. |
| `REQ-022` | 충족 | `JavaApplication#main`이 존재한다. |
| `REQ-023` | 미충족 | 관련 호출은 있으나 애플리케이션이 컴파일되지 않아 실행할 수 없다. |
| `REQ-024` | 미충족 | 관련 호출은 있으나 애플리케이션이 컴파일되지 않아 실행할 수 없다. |
| `REQ-025` | 미충족 | 관련 호출은 있으나 애플리케이션이 컴파일되지 않아 실행할 수 없다. |
| `REQ-026` | 미충족 | 싱글턴 구현을 제거했고 남은 확인 코드는 존재하지 않는 API를 호출한다. |
| `REQ-027` | 부분 충족 | `BasicMessageService#createMessage`에 검증은 있지만 JCF 팩토리가 검증용 Store를 공유하지 않는다. |
| `REQ-028` | 충족 | `BasicMessageService`가 세 Repository 계약을 생성자로 받는다. |
| `REQ-029` | 충족 | JCF/File Store가 데이터 부재를 원인이 드러나는 `IllegalStateException`으로 처리한다. |
| `REQ-030` | 부분 충족 | 두 구체 팩토리에 생성은 모였지만 호출 계약 불일치와 분리된 JCF Store 때문에 완전한 객체 그래프를 만들지 못한다. |
| `REQ-031` | 미충족 | 현재 소스에 `service.file.File*Service`가 없다. |
| `REQ-032` | 충족 | 세 엔티티가 `Serializable`을 구현한다. |
| `REQ-033` | 미충족 | 대상 `File*Service` 자체가 없다. |
| `REQ-034` | 미충족 | 대상 `File*Service` 자체가 없다. |
| `REQ-035` | 미충족 | 대상 `File*Service` 자체가 없다. |
| `REQ-036` | 미충족 | 대상 `File*Service`가 없고 현재 애플리케이션도 컴파일되지 않는다. |
| `REQ-037` | 부분 충족 | `HISTORY.md#H-002`에 책임 분리 과정은 있으나 현재 비교 대상 서비스 구현은 제거됐다. |
| `REQ-038` | 충족 | `Basic*Service`가 객체 생성·변경·관계 검증을 담당한다. |
| `REQ-039` | 충족 | `JCFObjectStore`, `FileObjectStore`가 CRUD 저장 로직을 담당한다. |
| `REQ-040` | 미충족 | 도메인별 Repository 계약 대신 `CrudRepository<T>` 하나만 존재한다. |
| `REQ-041` | 부분 충족 | 공통 CRUD 계약은 있으나 요구한 도메인별 계약은 없다. |
| `REQ-042` | 충족 | 두 Store의 중복·부재·수정·삭제 실패 정책이 `IllegalStateException`으로 일관된다. |
| `REQ-043` | 부분 충족 | 세 JCF Repository는 존재하지만 요구한 대응 도메인 Repository 계약은 없다. |
| `REQ-044` | 충족 | 세 JCF Repository가 `JCFObjectStore`에 JCF 저장 로직을 위임한다. |
| `REQ-045` | 부분 충족 | 세 File Repository는 존재하지만 요구한 대응 도메인 Repository 계약은 없다. |
| `REQ-046` | 충족 | 세 File Repository가 `FileObjectStore`에 직렬화 저장 로직을 위임한다. |
| `REQ-047` | 부분 충족 | 도메인별 경로는 분리돼 있으나 현재 빌드 실패로 재생성 동작을 재실행하지 못했다. |
| `REQ-048` | 충족 | 세 `Basic*Service`가 대응 서비스 인터페이스를 구현한다. |
| `REQ-049` | 충족 | 세 서비스가 `final CrudRepository`를 생성자 주입받고 내부에서 구체 Repository를 만들지 않는다. |
| `REQ-050` | 충족 | 비즈니스 로직은 Basic 서비스, 저장 로직은 Repository/Store에 분리돼 있다. |
| `REQ-051` | 충족 | `BasicMessageService#createMessage`가 User·Channel 존재를 Repository로 검증한다. |
| `REQ-052` | 미충족 | JCF 구성 코드가 컴파일되지 않고 서비스 간 JCF Store도 공유되지 않는다. |
| `REQ-053` | 미충족 | File 구성 코드가 컴파일되지 않아 재생성 조회를 실행하지 못한다. |
| `REQ-054` | 부분 충족 | 팩토리 교체 의도는 보이나 호출자가 존재하지 않는 정적 생성 API와 Getter에 결합돼 있다. |
| `REQ-055` | 부분 충족 | `HISTORY.md#H-002`에 분리 과정은 있으나 현재 구현 비교를 완결한 별도 정리는 확인되지 않는다. |

## 잘된 점

- `ServiceFactory`를 제품군 생성 계약으로 두고 JCF/File 팩토리를 분리한 방향은 추상 팩토리의
  기본 참여자 구조에 맞는다.
- `Basic*Service`와 구체 Repository의 `new`를 팩토리 구현 안으로 이동해
  `JavaApplication`이 제품 생성 순서를 몰라도 되게 하려는 경계가 명확하다.
- `Basic*Service`는 계속 Repository 계약을 생성자 주입받으므로 저장 기술로부터 분리돼 있다.
- 팩토리를 싱글턴으로 만들지 않는 선택 자체는 문제없다. 한 팩토리 인스턴스가 하나의
  공유 객체 그래프를 소유하도록 만들면 싱글턴 없이도 협력 객체의 생명주기를 일관되게 관리할 수 있다.

## 발견 사항

### R-004 Blocker: 팩토리 계약과 호출 코드가 달라 컴파일되지 않음

- 근거:
  - `ServiceFactory`는 인스턴스 메서드 `createUserService`,
    `createMessageService`, `createChannelService`만 선언한다.
  - `JavaApplication#main`은 존재하지 않는 정적 메서드 `createFileFactory`,
    `createJcFactory`와 Getter 세 개를 호출한다.
  - `JCFServiceFactory`와 `FileServiceFactory` 생성자는 `private`이고 외부 생성 API도 없어
    호출자가 구체 팩토리 인스턴스를 만들 방법이 없다.
  - `./gradlew clean test`의 `compileJava`에서 위 불일치로 8개 오류가 재현됐다.
- 영향: 애플리케이션과 테스트가 컴파일되지 않아 추상 팩토리 동작을 실행할 수 없다.
- 개선 방향: 팩토리 생성 경계를 하나로 정한다. 순수한 추상 팩토리만 사용할 경우 구체 팩토리의
  생성자를 공개하고 구성 루트가 구체 팩토리 하나를 선택한 뒤 `create*Service`를 호출한다.
  팩토리 자체의 생성까지 숨기려면 별도 정적 제공자는 추상 팩토리가 아니라 추가적인
  단순 팩토리 역할임을 구분한다.

### R-005 Blocker: JCF 제품군이 Repository를 공유하지 않아 관계 검증이 실패함

- 근거:
  - `JCFServiceFactory#createUserService`는 새 `JCFUserRepository`를 만든다.
  - `JCFServiceFactory#createChannelService`도 새 `JCFChannelRepository`를 만든다.
  - `JCFServiceFactory#createMessageService`는 다시 별개의 User·Channel Repository를 만든다.
  - 각 JCF Repository 기본 생성자는 자체 `JCFObjectStore`를 만들고,
    `JCFObjectStore`의 `data`는 인스턴스별 `new HashMap<>()`이다.
  - `BasicMessageService#createMessage`는 자신에게 주입된 별도 Repository에서
    User와 Channel을 조회한다.
- 영향: User/Channel 서비스로 정상 생성한 데이터도 Message 서비스에서는 찾지 못해
  유효한 메시지 생성이 실패한다.
- 개선 방향: 구체 팩토리 한 인스턴스가 User·Channel·Message Repository를 각각 한 번만 만들고,
  User/Channel 서비스와 Message 서비스에 동일한 User·Channel Repository 인스턴스를 주입한다.
  이 공유 범위는 싱글턴이 아니라 팩토리 인스턴스가 소유하는 객체 그래프 범위다.

### R-006 Major: 싱글턴 제거는 설계상 가능하지만 과제 요구사항과 남은 실행 코드가 충돌함

- 근거:
  - `REQUIREMENTS.md`의 목표와 `REQ-026`은 싱글턴 직접 구현 및 동작 확인을 명시한다.
  - 현재 `ServiceFactory`에는 싱글턴 상태나 `getInstance`가 없다.
  - `JavaApplication#main`은 여전히 `"싱글톤 확인"`을 출력하면서 새 팩토리 생성 의도의
    `createJcFactory` 결과와 참조 동일성을 비교한다.
- 영향: 추상 팩토리 구현이 정상화돼도 `REQ-026`은 미충족이고, 새 팩토리를 반환한다면
  출력 결과는 `false`가 된다.
- 개선 방향: 과제 요구를 유지한다면 싱글턴 학습 검증을 별도 대상으로 남긴다. 사용자의 설계
  선택이 공식 요구보다 우선이라면 싱글턴 확인 코드를 제거하고 `REQ-026` 미충족을 명시한다.
  추상 팩토리와 싱글턴은 대체 관계가 아니라 생성 책임과 인스턴스 개수를 각각 다루는 독립 패턴이다.

### R-007 Major: 추상 팩토리 계약이 구체 서비스 타입을 노출함

- 근거:
  - `ServiceFactory`의 세 생성 메서드는 `UserService`, `ChannelService`, `MessageService`가
    아니라 `BasicUserService`, `BasicChannelService`, `BasicMessageService`를 반환한다.
  - 호출 계층이 이미 서비스 인터페이스에 의존할 수 있도록 세 서비스 계약이 존재한다.
- 영향: 호출자가 제품의 구체 클래스 이름을 알게 되어 제품군 교체와 테스트 대역 사용이
  불필요하게 제한되고 추상 팩토리의 추상화 경계가 약해진다.
- 개선 방향: 추상 팩토리는 서비스 인터페이스 타입을 반환하고, 구체 팩토리만
  `Basic*Service` 생성 사실을 알게 한다.

### R-008 Minor: 팩토리 파일의 형식과 import가 정리되지 않음

- 근거:
  - `git diff --check`가 `ServiceFactory.java`, `FileServiceFactory.java`의 CRLF 줄을
    후행 공백으로 보고한다.
  - `FileServiceFactory`에는 JCF Repository와 엔티티·`CrudRepository` 미사용 import가 있고,
    JCF가 아닌 클래스에 `"기본 서비스 팩토리는 JCF로 구성"` 주석이 남아 있다.
  - `JCFServiceFactory`에는 `FileUserRepository`와 동작에 필요 없는 Lombok `@Getter`가 있다.
- 영향: 기능 원인은 아니지만 리뷰 노이즈가 생기고 팩토리 책임을 잘못 설명한다.
- 개선 방향: LF로 통일하고 미사용 import·주석·어노테이션을 제거한다.

## 종합 판단

현재 구현은 추상 팩토리 방향을 잡았지만 아직 정상 동작하는 상태는 아니다. 우선 `R-004`로
생성·호출 계약을 하나로 맞춘 뒤, `R-005`처럼 한 팩토리 인스턴스가 공유 Repository를 소유하도록
객체 그래프를 바로잡아야 한다. 싱글턴을 쓰지 않는 것 자체가 원인은 아니며, 핵심은 팩토리의
생명주기 안에서 협력 객체가 같은 저장 상태를 공유하는지다.
