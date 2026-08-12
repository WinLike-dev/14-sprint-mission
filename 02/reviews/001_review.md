# 001 스타일 리뷰

- 리뷰 일자: 2026-07-24
- 리뷰 범위: 현재 변경된 `entity`, `service`, `repository`, `factory`, `JavaApplication`
- 리뷰 관점: 들여쓰기, 줄바꿈, 필드·파라미터·예외 변수 이름, 동일 역할 코드의 표현 통일성
- 전체 판정: 경미한 수정 권장

## 검증

- `git diff --check`: 통과
- 탭 및 후행 공백 검사: 발견 없음
- 줄바꿈 형식: 검사한 Java 소스는 LF로 통일
- `./gradlew clean test`: 성공 (`test NO-SOURCE`)
- 100자 초과 줄: `JavaApplication` 3곳

## 요구사항 이름 확인

- `REQ-043`: `JCFUserRepository`, `JCFChannelRepository`, `JCFMessageRepository` 이름 충족
- `REQ-045`: `FileUserRepository`, `FileChannelRepository`, `FileMessageRepository` 이름 충족
- `REQ-048`: `BasicUserService`, `BasicChannelService`, `BasicMessageService` 이름 충족
- `JCF` 대문자 표기는 일반적인 약어 취향이 아니라 과제에서 지정한 이름이므로 유지

## 잘된 점

- 세 Repository 인터페이스가 `create`, `findById`, `findAll`, `update`, `deleteById`로 통일되어 있다.
- 세 JCF Repository가 저장 필드 이름을 `data`로 통일했다.
- 세 Basic 서비스가 도메인 이름을 접두어로 둔 Repository 필드 이름을 일관되게 사용한다.
- File Repository의 스트림·경로·예외 변수 이름이 각각 `inputStream`, `outputStream`, `filePath`, `exception`으로 통일되어 있다.
- 패키지, 클래스, 메서드, 상수는 Java 관례에 맞는 표기법을 사용한다.

## 발견 사항

### R-001 Minor: User 생성 파라미터가 `name`과 `username`으로 혼용됨

- 근거:
  - `User#username`, `User#User(String username, ...)`는 도메인 용어를 `username`으로 사용한다.
  - `UserService#createUser`는 첫 파라미터를 `name`으로 선언한다.
  - `JCFUserService#createUser`와 `FileUserService#createUser`도 `name`을 사용한다.
  - `BasicUserService#createUser`는 `username`을 사용한다.
- 영향: 같은 계약을 구현하는 클래스 사이에서 첫 인자의 의미를 다시 확인해야 한다.
- 개선 방향: 도메인 필드명에 맞춰 `UserService`와 모든 구현체의 생성 파라미터를 `username`으로 통일한다.

### R-002 Minor: 메서드와 표현식 줄바꿈 기준이 클래스마다 다름

- 근거:
  - `UserService`는 메서드 사이 빈 줄이 없지만 `ChannelService`와 `MessageService`는 빈 줄을 둔다.
  - `BasicUserService#createUser`와 `BasicChannelService#createChannel`은 100자 안에 들어오는 시그니처도 여러 줄로 나누지만 기존 JCF/File 구현은 한 줄을 사용한다.
  - `BasicChannelService`와 `ServiceFactory`에는 짧은 대입식의 `=` 뒤를 줄바꿈한 곳이 있다.
  - `JavaApplication` 33, 39, 46행은 각각 100자를 넘으며 예외 변수만 `e`를 사용한다.
- 영향: 기능상 문제는 없지만 코드의 리듬이 달라 동일 계층을 빠르게 비교하기 어렵다.
- 개선 방향:
  - 100자 이하 시그니처와 대입식은 한 줄, 초과하는 경우 인자 단위로 줄바꿈한다.
  - 서비스 인터페이스의 메서드 사이 빈 줄 사용 여부를 하나로 통일한다.
  - `JavaApplication`의 긴 호출은 인자 단위로 줄바꿈하고 예외 변수는 `exception`으로 맞춘다.

### R-003 Suggestion: 고정 저장 경로의 필드 의미를 이름과 한정자로 더 명확히 할 수 있음

- 근거:
  - 세 File Repository와 세 File 서비스의 저장 경로는 생성자마다 동일한 `Path.of("data", 도메인)` 값으로 초기화된다.
  - 같은 클래스의 고정 확장자는 `FILE_EXTENSION`이라는 `static final` 상수로 선언돼 있다.
  - 저장 경로는 `private final Path directory`라는 인스턴스 필드여서 인스턴스별로 달라질 수 있는 값처럼 보인다.
- 영향: 현재 동작에는 문제가 없지만 확장자와 경로가 모두 고정 설정이라는 사실이 필드 선언에서 동일하게 드러나지 않는다.
- 개선 방향: 경로 주입 계획이 없다면 `private static final Path DIRECTORY`로 통일한다. 테스트 경로를 생성자로 주입할 계획이 있다면 현재 인스턴스 필드를 유지하고 생성자 파라미터로 의도를 드러낸다.

## 종합

- 들여쓰기와 공백 형식은 안정적이다.
- 이름과 줄바꿈의 경미한 불일치만 정리하면 세 도메인과 두 저장 구현을 나란히 읽기 쉬워진다.
- 이번 리뷰에서는 구현 소스를 수정하지 않았다.
