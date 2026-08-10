# Assignment History

## H-001 현재 브랜치의 upstream 미설정

- 최초 발생: 2026-07-18
- 최종 갱신: 2026-07-18
- 상태: 진행 중
- 출처: `USER_REPORTED`, `CODE_VERIFIED`, `GIT`
- 관련 요구사항: `REQ-001`
- 관련 리뷰: 없음
- 관련 커밋: `4eaf00c`
- 관찰: `git push` 실행 시 현재 `김승호` 브랜치에 upstream이 없다는 오류가 발생했다.
- 재현 명령: `git push`
- 실패 지점: Git push 전 추적 브랜치 확인 단계
- 근거: `git branch -vv`에서 `main`은 `[origin/main]`을 표시하지만 `김승호`는 원격 추적 브랜치를 표시하지 않았다. Git 설정에도 `branch.main.remote`와 `branch.main.merge`만 있고 `branch.김승호.*` 항목은 없다. `git ls-remote --heads origin refs/heads/김승호` 결과가 비어 있어 현재 원격에는 같은 이름의 브랜치도 없다.
- 원인 상태: 확인
- 원인: 로컬에서 생성한 `김승호` 브랜치가 아직 원격에 게시되지 않았고 upstream 설정도 생성되지 않았다.
- 해결 방법: 최초 한 번 `git push -u origin 김승호`를 실행해 원격 브랜치를 만들고 추적 관계를 설정한다.
- 검증 대기: 명령 실행 후 `git branch -vv`에서 `[origin/김승호]`가 표시되고 일반 `git push`가 성공하는지 확인한다.

## H-002 File IO와 객체 직렬화 구현 방법 질문

- 최초 발생: 2026-07-20
- 최종 갱신: 2026-07-23
- 상태: 해결됨
- 출처: `USER_REPORTED`, `TOOL_OBSERVED`, `CODE_VERIFIED`
- 관련 요구사항: `REQ-032`, `REQ-033`, `REQ-034`, `REQ-035`, `REQ-036`
- 관련 리뷰: 없음
- 관련 커밋: 없음
- 관찰: 학습자가 File IO 기반 서비스 구현을 시작하면서 객체 직렬화와 역직렬화의 개념 및 CRUD 메서드에 적용하는 방법을 구체적으로 질문했다.
- 재현 명령: 해당 없음
- 실패 지점: 구현 전 개념 및 설계 단계
- 근거: 현재 `FileUserService`, `FileChannelService`, `FileMessageService`는 서비스 메서드 골격만 존재하고 저장 및 조회 로직이 구현되지 않았다. 현재 `User`, `Channel`, `Message`는 `Serializable`을 구현하지 않는다.
- 원인 상태: 확인
- 원인: 메모리 객체를 바이트 스트림으로 변환해 파일에 기록하고 다시 객체로 복원하는 흐름과 이를 CRUD에 대응시키는 방법이 아직 정해지지 않았다.
- 안내: 도메인 객체에 `Serializable` 계약을 추가하고, 도메인별 디렉터리에 UUID를 파일명으로 사용해 객체 하나를 파일 하나에 저장하는 방식으로 직렬화, 역직렬화, 전체 조회, 덮어쓰기, 삭제를 단계적으로 구현한다.
- 검증 대기: 한 도메인의 File 서비스 구현 후 생성, 서비스 재생성 뒤 조회, 수정, 전체 조회, 삭제 및 파일 손상 예외 처리를 실행해 확인한다.
- 후속 관찰 (2026-07-23): `IoOperation<T>`와 `AbstractFileService#handleIoException`을 추가해 File CRUD의 반복 예외 처리를 공통화하는 방법을 질문했다. 현재 `FileChannelService#createChannel`은 표현식 람다 안에 지역 변수 선언을 바로 작성해 `./gradlew test`의 `compileJava` 단계에서 `')' expected`, `';' expected`로 실패한다.
- 후속 진단 (2026-07-23): `IoOperation<T>#execute`는 결과 `T`를 반환하므로 생성·단건 조회·전체 조회에는 블록 람다 안에서 직렬화 또는 역직렬화를 수행하고 결과 객체를 반환해야 한다. 반환값이 없는 수정·삭제는 별도의 `IoTask` 계약을 두거나 `Void` 반환 정책을 명시해야 한다. 파일 부재는 기존 JCF 서비스와 동일한 `NoSuchElementException` 계약으로 변환하고, 그 밖의 `IOException`은 원인 예외를 보존한 저장소 예외로 변환하는 방향을 안내한다.
- 설계 판단 (2026-07-23): 현재 `File*Service`는 저장 로직을 처음 구현하고 비교하는 중간 결과물이므로 함수형 인터페이스와 추상 서비스까지 먼저 도입하지 않는다. 각 CRUD에 `try-with-resources`와 필요한 예외 변환을 직접 작성해 작업별 자원 범위와 실패 정책을 확인한 뒤, 실제로 동일한 예외 변환이 반복되는 부분만 후속 `File*Repository` 단계에서 공통화한다.
- 후속 안내 (2026-07-23): `FileChannelService`의 나머지 CRUD 구현 예시를 `data/channels/{UUID}.ser`에 객체 하나씩 저장하는 정책으로 구체화했다. 생성자에서 저장 디렉터리를 준비하고, 단건 조회는 역직렬화, 전체 조회는 `.ser` 파일 순회, 수정은 조회한 객체 변경 후 같은 경로에 덮어쓰기, 삭제는 해당 경로 제거로 구성하며 각 작업에서 파일 부재와 입출력 실패를 서비스 계약에 맞게 직접 변환하도록 안내했다.
- 해결 (2026-07-23): `FileIoOperation<R>`와 `AbstractFileService<T>`를 추가해 디렉터리 초기화, 객체 직렬화·역직렬화, 전체 조회, 덮어쓰기, 삭제와 예외 변환을 공통화했다. `FileUserService`, `FileChannelService`, `FileMessageService`는 도메인 객체 생성·수정과 Message 관계 검증만 담당하도록 정리했고, 세 엔티티의 `serialVersionUID` 오타를 수정했다.
- 검증 (2026-07-23): `./gradlew clean test`가 성공했다(`test NO-SOURCE`). 별도 임시 디렉터리의 JShell 스모크 테스트에서 세 File 서비스의 생성·단건 조회·전체 조회·수정·삭제, 새 인스턴스 재조회, 사용자·채널 ID 보존, Message 연관 데이터 부재 거부, 불변 전체 조회 결과, 삭제 후 `NoSuchElementException`을 확인했다. `JavaApplication`을 File 서비스로 구성하는 `REQ-036` 검증은 이번 구현 범위 밖의 후속 작업으로 남는다.
- 후속 설명 (2026-07-23): `FileIoOperation<R>#execute`에 `IOException`과 `ClassNotFoundException`을 선언한 이유를 질문했다. 람다 내부의 `Files.*`, 스트림 생성·종료, `ObjectInputStream#readObject`가 발생시키는 checked exception을 각 람다에서 잡지 않고 `AbstractFileService#executeFileOperation`까지 전달해 한곳에서 서비스 예외로 변환하기 위한 계약임을 설명했다.
- 후속 리팩터링 (2026-07-23): File I/O 이름 통일성과 `AbstractFileService`의 메서드 수·복잡도를 재검토했다. 작업공간에 별도 `FileIoService`는 없음을 확인했고, 함수형 인터페이스는 단일 작업을 나타내는 `FileIoOperation`을 유지했다. 공통 CRUD 메서드는 `create`, `read`, `readAll`, `update`, `delete`로 통일하고, 세 단계였던 예외 실행 메서드는 `execute` 하나로 합쳤다. 경로 계산과 역직렬화는 각각 `resolvePath`, `deserialize`로 명명해 역할을 드러냈다.
- 후속 검증 (2026-07-23): `./gradlew clean test`가 성공했고(`test NO-SOURCE`), 임시 디렉터리 스모크 테스트에서 리팩터링 후 Message 생성·조회·수정·삭제 흐름이 `FILE_SERVICE_RENAME_OK`로 통과했다.
- 설계 보정 (2026-07-23): 모든 파일 작업을 `FileIoOperation<R>#execute`로 전달하자 직렬화만 수행하는 생성·수정·삭제에도 역직렬화 전용 `ClassNotFoundException` 계약이 함께 적용되고, 작업별 실패 의미가 `AbstractFileService#execute`에서 과도하게 합쳐졌다. 공통 CRUD 저장 구조를 제공하는 `AbstractFileService<T>`는 유지하되 `FileIoOperation`, 범용 `execute`, 단순 경로 계산용 `resolvePath`를 제거했다. 각 CRUD가 경로를 직접 만들고 실제로 발생 가능한 예외만 가까운 위치에서 변환하도록 변경했으며, 읽기 작업에서 실질적으로 반복되는 `deserialize`만 공통 메서드로 남겼다.
- 설계 보정 검증 (2026-07-23): `./gradlew clean test`가 성공했고(`test NO-SOURCE`), 별도 임시 디렉터리의 JShell 스모크 테스트가 `ABSTRACT_FILE_SERVICE_OK`로 통과했다. 서비스 재생성 후 조회, Message 생성·수정·전체 조회·삭제, 삭제된 파일의 `NoSuchElementException`, 손상된 직렬화 파일의 `IllegalStateException`을 확인했다.
- 후속 구조 분석 (2026-07-23): 각 `File*Service`가 File CRUD를 직접 구현하던 구조와 비교하면 `AbstractFileService<T>`는 현재 동일한 경로·직렬화·예외 정책의 중복을 줄이지만, 구체 서비스가 상위 클래스의 파일 저장 방식과 `data/{directory}/{UUID}.ser` 규칙에 강하게 결합된다. 특정 도메인만 저장 형식, 경로, 정렬, 동시성 또는 예외 정책이 달라지면 상위 CRUD를 재정의하거나 추상 클래스에 조건을 추가하게 될 위험이 있다. 또한 서비스 계층에 저장 로직이 남아 있어 `REQ-040` 이후 Repository 분리 시 구조를 다시 이동해야 한다.
- 구조 복구 (2026-07-23): Repository 분리 전 단계에서 저장 로직을 명시적으로 확인할 수 있도록 `AbstractFileService<T>`를 제거하고, 사용자가 만든 `FileUserService`, `FileChannelService`, `FileMessageService` 골격 안에 도메인별 디렉터리 준비, 직렬화 기반 CRUD, 파일 부재 및 처리 실패 예외 변환을 각각 직접 배치했다. `FileMessageService`의 생성자 주입과 연관 User·Channel 존재 검증은 요구사항에 따라 유지했다.
- 구조 복구 검증 (2026-07-23): `./gradlew clean test`가 성공했고(`test NO-SOURCE`), 별도 임시 디렉터리의 JShell 스모크 테스트가 `DIRECT_FILE_SERVICES_OK`로 통과했다. 세 도메인의 생성·서비스 재생성 후 조회·수정·전체 조회·삭제와 Message 연관 데이터 부재 거부를 확인했다.
- 예외 정책 변경 (2026-07-23): 사용자의 요청에 따라 세 `File*Service`가 외부로 전달하는 실패 타입을 `IllegalStateException`으로 통일했다. 파일 부재도 `NoSuchElementException` 대신 원인 `NoSuchFileException`을 포함한 `IllegalStateException`으로 변환하고, `FileMessageService#createMessage`가 JCF 서비스에서 받은 연관 데이터 부재도 같은 타입으로 변환한다. 이 정책은 단순하지만 호출자가 예외 타입만으로 데이터 부재와 파일 손상·입출력 실패를 구분할 수 없는 절충이 있다.
- 예외 정책 변경 중 관찰 및 수정 (2026-07-23): 첫 `./gradlew clean test`에서 `FileChannelService#deleteChannel`의 `IllegalStateException` 생성자 인자 사이 쉼표 누락과 남은 `notFound` 처리문 때문에 `compileJava`가 실패했다. 불필요한 처리문을 제거하고 원인 예외를 생성자 두 번째 인자로 전달하도록 수정했다.
- 예외 정책 검증 (2026-07-23): 수정 후 `./gradlew clean test`가 성공했고(`test NO-SOURCE`), 임시 디렉터리의 JShell 스모크 테스트가 `FILE_ILLEGAL_STATE_POLICY_OK`로 통과했다. 정상 Message 생성·수정·조회, 세 도메인의 파일 부재, 손상된 User 파일, JCF 의존성을 주입한 Message 연관 데이터 부재가 모두 예상한 `IllegalStateException`으로 처리되는 것을 확인했다.
- Repository 구현 질문 (2026-07-23): `REQ-040`~`REQ-047`을 기준으로 Repository 코드를 요청했다. 생성과 수정을 하나의 upsert 메서드로 합치면 중복 생성과 존재하지 않는 대상 수정 정책이 흐려지므로, 도메인별 Repository 계약을 `create`, `findById`, `findAll`, `update`, `deleteById`로 구성하고 JCF 구현은 `Map`, File 구현은 도메인별 디렉터리와 객체 직렬화를 사용하도록 안내한다. 예외 정책은 현재 File 서비스 선택과 맞춰 `IllegalStateException`으로 통일한다.
- Repository 후속 설명 (2026-07-23): 전체 구현 예시보다 이해하기 쉬운 설명을 요청했다. `User` 하나를 기준으로 Repository 인터페이스는 저장 방식과 무관한 명령 목록, JCF/File Repository는 각각 그 명령의 저장 기술별 구현, Basic 서비스는 객체 생성·변경 후 Repository를 호출하는 협력 구조로 단순화해 설명한다.
- Repository 구조 안내 (2026-07-23): 코드 작성 전에 디렉터리 구조와 전체 방향만 요청했다. `repository` 아래에 도메인별 계약, `repository.jcf`와 `repository.file` 아래에 저장 방식별 구현, `service.basic` 아래에 Repository를 주입받는 비즈니스 서비스를 배치한다. 기존 `service.jcf`와 `service.file`은 요구사항 비교를 위해 당장 삭제하지 않고, 새 구조 검증 후 이전 구현과 책임·교체 가능성을 비교하는 자료로 유지한다.
- Basic 서비스 계약 안내 (2026-07-23): `Basic`과 인터페이스를 어떻게 구성할지 질문했다. 별도의 `BasicUserService` 인터페이스를 추가하지 않고 기존 `UserService`, `ChannelService`, `MessageService`를 애플리케이션 서비스 계약으로 유지하며, `Basic*Service`가 이를 구현하고 필요한 도메인 Repository 인터페이스를 생성자 주입받도록 안내한다. 이 구성은 호출자가 Basic/JCF/File 같은 서비스 구현 이름을 알지 않게 하고, Basic 서비스 내부에서도 JCF/File Repository 구현을 알지 않게 한다.
- Basic 서비스 구현 안내 (2026-07-23): 세 `Basic*Service`의 구체적인 구성 예시를 요청했다. User와 Channel 서비스는 각 도메인 Repository 하나를 주입받아 객체 생성·변경만 수행하고, Message 서비스는 Message·User·Channel Repository를 주입받아 메시지 생성 전에 연관 도메인의 존재를 검증한 뒤 Message Repository에 저장하도록 구성한다.
- Repository와 Basic 서비스 구현 (2026-07-23): 기존 엔티티·서비스 인터페이스·`service.jcf`·`service.file`을 유지하고 사용자가 만든 Repository 및 Basic 서비스 골격을 채우는 방식으로 변경 범위를 제한했다. 세 Repository 계약을 `create`, `findById`, `findAll`, `update`, `deleteById`로 통일하고 JCF 구현은 도메인별 `Map`, File 구현은 `data/{domain}/{UUID}.ser` 직렬화를 사용한다. 세 Basic 서비스는 Repository 인터페이스를 생성자 주입받으며 Message 생성의 User·Channel 존재 검증은 `BasicMessageService`에 둔다.
- 실행 구성 변경 (2026-07-23): 기존 `ServiceFactory#getInstance` 외부 API와 싱글톤을 유지하면서 내부 구성을 `Basic*Service + JCF*Repository`로 교체하고 `createFileFactory`만 추가했다. `JavaApplication`은 기본 JCF 구성과 `file` 인자의 File 구성을 동일한 CRUD 코드로 실행하며, File 모드에서는 Factory와 서비스를 다시 생성한 뒤 이전 Message를 조회한다.
- Repository 구현 검증 (2026-07-23): `./gradlew clean test`가 성공했다(`test NO-SOURCE`). `JavaApplication` 기본 실행에서 JCF 기반 전체 CRUD·관계 검증·싱글톤을 확인했고, 임시 디렉터리의 `JavaApplication file` 실행에서 같은 CRUD와 서비스 재생성 후 `hello, world` Message 조회를 확인했다. 추가 JShell 검증 `REPOSITORY_POLICY_OK`에서 JCF/File의 중복 생성, 없는 대상 수정·삭제가 `IllegalStateException`으로 실패하고 전체 조회 결과가 불변이며 새 File Repository가 기존 User를 조회하는 것을 확인했다.
- File Repository 경로 상수 질문 (2026-07-23): 도메인별 저장 디렉터리 `Path`를 `static`으로 둘 수 있는지 질문했다. 현재 경로는 인스턴스별 상태가 아니라 클래스의 모든 인스턴스가 공유하는 고정 설정이므로 `private static final Path DIRECTORY`가 적합하다. 다만 디렉터리 생성 실패를 일반 생성 흐름에서 명확히 처리하고 정적 초기화 실패를 피하기 위해 `Files.createDirectories(DIRECTORY)` 호출은 생성자에 유지하도록 안내한다.

## H-003 변경 소스 스타일 통일성 리뷰

- 최초 발생: 2026-07-24
- 최종 갱신: 2026-07-24
- 상태: 해결됨
- 출처: `USER_REPORTED`, `CODE_VERIFIED`, `TOOL_OBSERVED`, `REVIEW`
- 관련 요구사항: `REQ-043`, `REQ-045`, `REQ-048`
- 관련 리뷰: `R-001`, `R-002`, `R-003` (`reviews/001_review.md`)
- 관련 커밋: 없음
- 관찰: Repository·Basic 서비스 구현 후 들여쓰기와 이름의 통일성 검사를 요청했다.
- 재현 명령: `git diff --check`, `./gradlew clean test`
- 실패 지점: 컴파일 또는 실행 실패는 없으며 가독성과 명명 일관성에서 경미한 차이가 확인됐다.
- 근거: `UserService#createUser` 및 JCF/File 구현은 `name`, `BasicUserService#createUser`와 `User#username`은 `username`을 사용한다. 서비스 인터페이스와 Basic 구현 사이에 빈 줄 및 줄바꿈 기준이 다르고 `JavaApplication`에는 100자 초과 줄 3곳과 예외 변수 `e`가 있다. 고정 저장 경로는 인스턴스 필드 `directory`인 반면 확장자는 정적 상수이다.
- 원인 상태: 확인
- 원인: 기능 구현 과정에서 기존 코드의 한 줄 스타일과 새 코드의 인자별 줄바꿈 스타일이 함께 사용됐고, User 도메인의 기존 `name` 용어와 엔티티의 `username` 용어를 각 구현이 다르게 따랐다.
- 검증: 탭·후행 공백·CRLF 혼용은 발견되지 않았고 `git diff --check`와 `./gradlew clean test`는 통과했다. `test`는 `NO-SOURCE`이다.
- 개선 대기: `R-001`~`R-003` 중 적용할 항목을 정한 뒤 소스 포맷과 이름을 일괄 수정하고 재검증한다.
- 수정 선택 (2026-07-24): 사용자가 `R-001`, `R-002`만 적용하고 `R-003`은 변경하지 않도록 지정했다.
- 수정 (2026-07-24): `UserService`, `JCFUserService`, `FileUserService`, `BasicUserService`의 생성 파라미터를 `username`으로 통일했다. 서비스 인터페이스의 빈 줄, Basic 서비스의 100자 이하 선언과 짧은 대입식, `ServiceFactory`의 짧은 대입식, `JavaApplication`의 긴 호출과 예외 변수 이름을 동일 기준으로 정리했다.
- 재검증 (2026-07-24): `git diff --check`와 `./gradlew clean test`가 성공했고 검사한 Java 소스에서 100자 초과 줄·탭·후행 공백이 발견되지 않았다. `JavaApplication`의 JCF/File 모드가 모두 정상 실행됐으며 File 모드의 서비스 재생성 후 Message 조회도 성공했다.
- 재리뷰: `R-001` 해결됨, `R-002` 해결됨, `R-003` 요청 범위 제외로 미해결 (`reviews/002_re_review.md`)
- 최종 상태: 요청 범위 해결됨
- Repository 재사용성 질문 (2026-07-24): 여섯 Repository 구현의 CRUD 구조가 동일하므로 재사용성을 높이는 구성을 질문했다. 도메인별 Repository 인터페이스와 Basic 서비스는 요구사항 및 비즈니스 차이를 드러내기 위해 유지하고, 공통 식별자 계약 `Identifiable`, `AbstractJCFRepository<T>`, `AbstractFileRepository<T>`를 Repository 구현 계층에만 적용하는 방향을 안내한다. File 추상 구현은 디렉터리·도메인 타입·표시 이름을 생성자로 받고 CRUD별 예외 처리를 직접 유지하며 함수형 인터페이스나 람다는 사용하지 않는다.
- 조합 방식 후속 안내 (2026-07-24): 추상 Repository 상속 대신 공통 저장 객체를 조합하는 구체적인 구조를 요청했다. `FileObjectStore<T>`와 `JCFObjectStore<T>`가 저장 기술별 CRUD를 소유하고, `File*Repository`와 `JCF*Repository`는 해당 객체를 필드로 보유하면서 도메인 Repository 계약의 호출을 위임하도록 안내한다. 공통 객체가 식별자를 사용할 수 있도록 엔티티에는 `Identifiable#getId` 계약만 추가하며, Basic 서비스와 도메인별 Repository 인터페이스는 변경하지 않는다.
- 조합 구조 구현 전 관찰 (2026-07-24): 사용자가 공식 요구사항보다 단순한 조합 구조를 우선하도록 요청하고 불필요한 인터페이스와 추상 클래스 제거를 허용했다. 구현 전 `./gradlew clean test`는 이미 삭제된 `UserRepository`, `ChannelRepository`, `MessageRepository`를 Basic 서비스, Factory, 구체 Repository가 계속 참조해 `compileJava`에서 18개 오류로 실패했다.
- 조합 구조 구현 (2026-07-24): 공통 계약을 `CrudRepository<T>` 하나로 통일하고, `FileObjectStore<T>`와 `JCFObjectStore<T>`가 저장 기술별 CRUD를 담당하도록 구현했다. 여섯 구체 Repository는 Store를 생성자 주입받아 호출을 위임하고, Basic 서비스와 `ServiceFactory`는 `CrudRepository<User|Channel|Message>`에 의존하도록 변경했다. `BasicRepository`, `AbstractFileRepository`, `AbstractJCFRepository`는 제거했으며 엔티티 세 개는 `Identifiable`을 구현한다. 이 선택은 사용자의 지시에 따라 단순성을 우선하므로 도메인별 Repository 인터페이스를 요구하는 `REQ-040`에는 맞지 않는다.
- 조합 구조 검증 (2026-07-24): `git diff --check`와 100자 초과 줄·후행 공백·CRLF 검사가 통과했고 `./gradlew clean test`가 성공했다(`test NO-SOURCE`). 별도 임시 작업 디렉터리에서 `JavaApplication`의 JCF/File 모드를 실행해 User·Channel·Message CRUD, Message 관계 검증, JCF 싱글톤, File 서비스 재생성 후 메시지 조회를 확인했다.
- 커밋 구성 및 게시 (2026-07-24): 기존 `0a78633` 이하 커밋을 수정하지 않고 `sprint-02-jcf-crud`에 `4e6df1b`(File 서비스), `ee84b45`(Basic 서비스와 CRUD 계약), `474d81e`(Repository 전환과 기존 File/JCF 서비스 제거), `32dcf11`(공통 Store 조합) 순서로 커밋했다. 각 중간 커밋에서 `./gradlew clean test`를 통과했고 Repository 전환 및 최종 커밋에서는 JCF/File 실행까지 확인했다. `git push origin sprint-02-jcf-crud`가 fast-forward로 성공했으며 `김승호-02`와 `origin/김승호-02`는 `0a78633`을 유지한다. GitHub의 origin 및 upstream 저장소에서 해당 head 브랜치와 연결된 PR은 발견되지 않았다.
- PR 템플릿 게시 (2026-07-24): 기존 구현 커밋은 수정하지 않고 `60fa8e8`(`chore: 요구사항 기반 PR 템플릿 수정`)을 마지막에 추가했다. `.github/PULL_REQUEST_TEMPLATE.md`에서 `REQ-001`~`REQ-055`를 개별 점검할 수 있게 하고 미충족 또는 다르게 구현한 요구사항과 검증 결과를 기록하는 구역을 추가했다. `git diff --check`와 요구사항 번호 55개의 중복·누락 검사를 통과했으며 `origin/sprint-02-jcf-crud`로 fast-forward 푸시한 뒤 로컬과 원격 HEAD가 일치함을 확인했다.
- 커밋 메시지 재작성 (2026-07-24): 사용자 요청에 따라 `0a78633` 이후 다섯 커밋의 구분과 파일 내용을 유지하면서 메시지에 영어 용어를 혼합했다. SHA는 `4e6df1b`→`30e573a`, `ee84b45`→`074210f`, `474d81e`→`be638cf`, `32dcf11`→`a95473c`, `60fa8e8`→`9ab3de3`으로 변경됐다. 재작성 전 HEAD는 로컬 `backup/sprint-02-jcf-crud-before-reword-60fa8e8` 브랜치에 보존했고, 이전·이후 tree 해시가 동일함을 확인한 뒤 확인된 원격 SHA를 조건으로 `--force-with-lease` 푸시했다. 로컬과 원격 `sprint-02-jcf-crud`는 `9ab3de3`으로 일치하며 `김승호-02`와 기존 stash는 변경하지 않았다.
- 커밋 메시지 전체 정리 (2026-07-24): 사용자가 범위를 2026-07-21 커밋까지 확장하고 이름만 자연스러운 영어로 바꾸도록 요청했다. `280b884` 이후 12개 커밋의 패치와 구분을 유지하면서 메시지를 영어 Conventional Commit 형식으로 재작성했고 새 HEAD는 `6fa7b40`이다. 재작성 전 `9ab3de3`은 로컬 `backup/sprint-02-jcf-crud-before-reword-through-20260721-9ab3de3` 브랜치에 보존했다. 이전·이후 tree 해시가 동일함을 확인하고 `--force-with-lease`로 `origin/sprint-02-jcf-crud`를 갱신했으며, `김승호-02`와 기존 stash는 변경하지 않았다.
- 커밋 메시지 한영 혼합 정리 (2026-07-24): 사용자의 후속 요청에 따라 같은 12개 커밋을 기술 용어는 영어, 동작 설명은 한국어인 Conventional Commit 메시지로 다시 작성했다. 파일 내용과 커밋 구분은 유지했고 재작성 전·후 tree 해시가 동일함을 확인했다. 재작성 전 `6fa7b40`은 로컬 `backup/sprint-02-jcf-crud-before-natural-ko-en-6fa7b40`에 보존했으며 `--force-with-lease`로 원격을 갱신한 뒤 새 HEAD `ae7931c`의 로컬·원격 일치를 확인했다.
- 1차·2차 PR 템플릿 반영 (2026-07-24): `.github/PULL_REQUEST_TEMPLATE.md`에 적용 차수 선택란을 추가하고 `REQ-001`~`REQ-030`을 1차, `REQ-031`~`REQ-055`를 2차로 분리했다. 55개 요구사항 번호의 중복·누락 검사와 `git diff --check`를 통과한 뒤 `f0e6704`(`chore: 1차·2차 requirements를 PR 템플릿에 반영`)로 커밋해 `origin/sprint-02-jcf-crud`에 fast-forward 푸시했다.
- 원본 요구사항 PR 템플릿 반영 (2026-07-24): 사용자가 요약된 완료 조건 대신 기존 1차·2차 과제 원문을 하나의 PR 템플릿에 합치도록 요청했다. 1차의 프로젝트 초기화, 도메인 모델링, JCF 서비스, 메인 실행, 서비스 간 의존성 주입과 2차의 File IO, 구현체 분석, JCF/File Repository, Basic 서비스 의존성 주입 및 구현체별 테스트·비교를 77개 체크박스로 구성했다. 필수 원문 표식 검사와 `git diff --check`를 통과한 뒤 `3ad58b8`(`chore: 원본 requirements를 PR 템플릿에 반영`)로 커밋해 `origin/sprint-02-jcf-crud`에 fast-forward 푸시했다.
- PR 템플릿 커밋 메시지 수정 (2026-07-24): 사용자 요청에 따라 최신 커밋 메시지를 `chore: 1차 2차 요구사항 PR 템플릿에 반영`으로 amend했다. 이전·이후 tree 해시가 동일해 파일 내용은 변경되지 않았고 SHA는 `3ad58b8`에서 `57336cd`로 변경됐다. 이전 커밋은 로컬 `backup/pr-template-before-amend-3ad58b8`에 보존했으며 확인된 원격 SHA를 조건으로 `--force-with-lease` 푸시했다.
- PR 템플릿 커밋 통합 (2026-07-24): 사용자가 템플릿 관련 `chore`를 하나로 줄이도록 요청했다. 코드 이력의 마지막 `1a2b86e` 위에 `ae7931c`, `f0e6704`, `57336cd`의 패치를 커밋 없이 순서대로 적용한 뒤 `979f213`(`chore: 1차 2차 요구사항 PR 템플릿에 반영`) 하나로 커밋했다. 통합 전·후 tree 해시가 동일하고 최종 템플릿 내용이 유지됨을 확인했으며, 이전 HEAD는 로컬 `backup/before-squash-template-commits-57336cd`에 보존한 뒤 `--force-with-lease`로 원격을 갱신했다.
- PR 템플릿 축약 (2026-07-24): 사용자가 1차 과제 템플릿처럼 핵심 내용만 남기도록 요청했다. 상세 원문 체크리스트를 제거하고 기본·심화 요구사항, 주요 변경사항, 테스트, 멘토에게 구역으로 축약했으며 File IO, Repository, Basic Service와 관계 검증 핵심은 유지했다. 템플릿은 32줄·12개 체크박스로 줄었고 `git diff --check`를 통과했다. 새 커밋을 추가하지 않고 기존 단일 템플릿 커밋을 amend해 SHA가 `979f213`에서 `869662f`로 변경됐으며 `--force-with-lease`로 원격에 반영했다. 중단된 PR 생성 요청으로 생성된 PR은 없다.
- PR 템플릿 주요 변경사항 입력 (2026-07-24): 사용자가 빈 주요 변경사항 대신 1차·2차 핵심 변경을 넣도록 요청했다. 1차는 도메인·CRUD Service 계약과 JCF·관계 검증·실행 구성, 2차는 File IO 영속화·Repository 분리와 Basic Service·ObjectStore 조합으로 각각 두 항목씩 요약했다. 새 커밋을 추가하지 않고 기존 템플릿 커밋을 amend해 SHA가 `869662f`에서 `3f3d96d`로 변경됐으며 `--force-with-lease`로 원격에 반영했다.
- PR 템플릿 주요 변경사항을 커밋 이력과 동기화 (2026-07-24): 사용자가 요약 항목 대신 실제 커밋 내용을 모두 넣되 `chore`를 제외하고 `feat`와 `refactor`만 사용하도록 요청했다. Git 이력에서 `feat` 7개와 `refactor` 2개를 순서대로 추출해 주요 변경사항에 그대로 반영하고 `style`과 `chore`는 제외했다. 새 커밋을 추가하지 않고 기존 템플릿 커밋을 amend해 SHA가 `3f3d96d`에서 `69d1caf`로 변경됐으며 `--force-with-lease`로 원격에 반영했다.
- PR 템플릿 체크 완료 표시 (2026-07-24): 사용자가 모든 체크박스를 완료 상태로 표시하도록 요청했다. `./gradlew clean test classes`가 성공했고(`test NO-SOURCE`), 별도 임시 디렉터리에서 JavaApplication의 JCF 모드와 File 모드를 실행해 CRUD, Message 관계 검증, JCF 싱글톤, File 서비스 재생성 후 데이터 조회를 확인했다. 요구사항 및 테스트 체크박스 12개를 모두 `[x]`로 변경하고 기존 템플릿 커밋을 amend해 SHA가 `69d1caf`에서 `f4f7e03`으로 변경됐으며 `--force-with-lease`로 원격에 반영했다.
- PR 템플릿 ObjectStore 변경사항 문구 수정 (2026-07-24): 사용자가 주요 변경사항의 마지막 항목을 `refactor: 공통 ObjectStore를 도입 Repository 중복 저장 로직 제거`로 변경하도록 요청했다. 기존 템플릿 커밋을 amend해 SHA가 `f4f7e03`에서 `f7cff6f`로 변경됐으며 `--force-with-lease`로 원격에 반영했다.

## H-004 추상 팩토리 전환 후 구성 실패

- 최초 발생: 2026-07-26
- 최종 갱신: 2026-07-26
- 상태: 진행 중
- 출처: `USER_REPORTED`, `CODE_VERIFIED`, `TOOL_OBSERVED`, `REVIEW`, `GIT`
- 관련 요구사항: `REQ-002`, `REQ-026`, `REQ-027`, `REQ-030`, `REQ-052`, `REQ-053`, `REQ-054`
- 관련 리뷰: `R-004`, `R-005`, `R-006`, `R-007`, `R-008` (`reviews/003_review.md`)
- 관련 커밋: 없음
- 관찰: 싱글턴을 제거하고 추상 팩토리만 사용하도록 전환한 현재 작업 트리가 문제없이 동작하는지 검토를 요청했다.
- 재현 명령: `./gradlew clean test`, `git diff --check`
- 실패 지점: `compileJava`에서 `JavaApplication`이 `ServiceFactory`에 없는 정적 생성 메서드와 Getter를 호출해 8개 오류가 발생한다.
- 근거: `ServiceFactory`는 `create*Service` 인스턴스 메서드만 선언하지만 `JavaApplication#main`은 `createFileFactory`, `createJcFactory`, `get*Service`를 호출한다. 두 구체 팩토리의 생성자는 `private`이고 외부 생성 API도 없다. 또한 `JCFServiceFactory`의 세 서비스 생성 메서드가 각각 새 Repository를 만들며 각 JCF Repository는 별도의 `JCFObjectStore`와 `HashMap`을 소유하므로 Message 관계 검증이 User·Channel 서비스가 저장한 데이터를 볼 수 없다.
- 원인 상태: 확인
- 원인: 기존 싱글턴 Factory의 정적 선택·Getter API를 추상 팩토리의 인스턴스 생성 메서드 계약으로 바꾸는 과정에서 호출 코드를 함께 맞추지 않았고, 기존 Factory가 한 번 생성해 공유하던 Repository 객체 그래프도 서비스별 생성으로 분리됐다.
- 추가 판단: 싱글턴 제거 자체는 구성 실패의 원인이 아니다. 일반 팩토리 인스턴스도 공유 Repository를 소유할 수 있다. 다만 싱글턴 직접 구현을 요구하는 `REQ-026`은 현재 명시적으로 미충족이다.
- 검증: `./gradlew clean test` 실패, 테스트 소스 없음. `git diff --check`는 두 팩토리 파일의 CRLF 줄바꿈을 후행 공백으로 감지했다.
- 개선 대기: 팩토리 생성 API와 서비스 획득 API를 한 계약으로 통일하고, 한 JCF 팩토리 인스턴스가 만든 동일 User·Channel Repository를 관련 서비스가 공유하도록 수정한 뒤 빌드와 JCF/File 실행을 재검증한다.
