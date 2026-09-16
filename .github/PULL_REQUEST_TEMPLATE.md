요구사항
---

### 데이터베이스

- [x] 데이터베이스 환경 설정 (`discodeit` / `discodeit_user`)
- [x] ERD를 옮긴 DDL을 `/src/main/resources/schema.sql`에 작성

### Spring Data JPA 적용

- [x] Spring Data JPA · PostgreSQL 의존성 추가
- [x] DB 접속 설정과 SQL 로그 설정
- [x] 공통 속성을 추상 클래스로 정의하고 상속 (`common/entity/base`)
- [x] `createdAt` / `updatedAt` 자동 설정
- [x] 클래스 다이어그램에 맞춘 참조 관계 수정
- [x] 연관관계 매핑 정보 정리 (아래)
- [x] JPA 어노테이션으로 ERD·연관관계 반영
- [x] 외래키 제약과 부모-자식 관계를 고려한 `cascade`, `orphanRemoval`

### 레포지토리와 서비스

- [x] Repository를 `JpaRepository`로 정의하고 쿼리 메서드로 대체, File/JCF 구현체 삭제
- [x] 영속성 컨텍스트에 맞춘 서비스 레이어 수정 (트랜잭션, 영속성 전이, 변경 감지, 지연 로딩)

### DTO

- [x] Entity를 그대로 노출할 때의 문제점 정리 (아래)
- [x] 클래스 다이어그램에 맞춘 DTO 정의
- [x] 매핑을 담당하는 Mapper 컴포넌트 정의

### BinaryContent 저장 로직 고도화

- [x] `bytes` 속성 제거 (메타 정보만 저장)
- [x] `BinaryContentStorage` 인터페이스 설계
- [x] 서비스 레이어가 스토리지를 사용하도록 리팩터링
- [x] 다운로드 API 추가 (`GET /api/binaryContents/{binaryContentId}/download`)
- [x] 로컬 디스크 구현체 (`discodeit.storage.type=local`일 때만 Bean 등록)

### 페이징과 정렬

- [x] 메시지 목록을 최근 순 50개씩 조회 (전체 개수는 세지 않음)
- [x] 제네릭 `PageResponse<T>` 정의
- [x] `Slice` / `Page`에서 DTO를 만드는 `PageResponseMapper`

---

## 연관관계 매핑 정보

| 엔티티 관계 | 다중성 | 방향성 | 부모-자식 관계 | 연관관계의 주인 | 영속성 전이 / 고아 객체 |
|---|---|---|---|---|---|
| User : BinaryContent (프로필) | 1:1 | User→BinaryContent 단방향 | 부모: User, 자식: BinaryContent | User (`users.profile_id`) | `PERSIST`, `REMOVE` / `orphanRemoval` |
| User : UserStatus | 1:1 | 양방향 | 부모: User, 자식: UserStatus | UserStatus (`user_statuses.user_id`) | `PERSIST`, `REMOVE` / `orphanRemoval` |
| User : ReadStatus | 1:N | ReadStatus→User 단방향 | 부모: User, 자식: ReadStatus | ReadStatus (`read_statuses.user_id`) | 없음 (서비스가 벌크 삭제) |
| Channel : ReadStatus | 1:N | ReadStatus→Channel 단방향 | 부모: Channel, 자식: ReadStatus | ReadStatus (`read_statuses.channel_id`) | 없음 (서비스가 벌크 삭제) |
| Channel : Message | 1:N | Message→Channel 단방향 | 부모: Channel, 자식: Message | Message (`messages.channel_id`) | 없음 (서비스가 삭제) |
| User : Message (작성자) | 1:N | Message→User 단방향 | 부모 없음 (메시지는 작성자에 종속되지 않음) | Message (`messages.author_id`) | 없음 (탈퇴 시 작성자만 `null`) |
| Message : BinaryContent (첨부) | 1:N | Message→BinaryContent 단방향 | 부모: Message, 자식: BinaryContent | Message (`message_attachments` 조인 테이블) | `PERSIST`, `REMOVE` / `orphanRemoval` |

정리하면서 세운 기준입니다.

- **주인은 외래키를 가진 쪽**으로 뒀습니다. ERD에서 FK가 어디 있는지가 그대로 주인을 정합니다.
- **부모-자식은 "혼자 존재할 수 있는가"로 판단**했습니다. 프로필 이미지·온라인 상태·첨부파일은 주인이 사라지면 존재할 이유가 없어 `cascade`와 `orphanRemoval`을 걸었고, 읽음 상태와 메시지는 사용자·채널이 사라져도 개념적으로 별개라 전이를 걸지 않고 서비스가 순서를 정해 지웁니다.
- **메시지와 작성자는 부모-자식이 아닙니다.** 사용자가 탈퇴해도 대화 기록은 남아야 하므로 `ON DELETE SET NULL`에 맞춰 작성자만 비웁니다.
- **양방향은 한 곳(User↔UserStatus)만** 뒀습니다. `User`를 만들 때 상태도 함께 만들어야 해서 참조가 필요했고, 나머지는 한쪽 방향만으로 충분해 단방향으로 뒀습니다.
- 모든 `@ManyToOne` / `@OneToOne`은 `LAZY`입니다. 다만 `mappedBy` 쪽 `@OneToOne`(`User.status`)은 JPA 특성상 지연 로딩이 적용되지 않아, 조회가 따라붙는 경로에서는 `getReferenceById`나 `@EntityGraph`로 피했습니다.

---

## Entity를 Controller까지 그대로 노출하면 생기는 문제

DTO를 두면 클래스와 매핑 코드가 늘어납니다. 그럼에도 둔 이유를 이번 미션에서 실제로 부딪힌 순서대로 적습니다.

**1. 민감한 데이터가 새어 나갑니다.** `User`에는 `password`가 있습니다. 엔티티를 그대로 직렬화하면 응답에 비밀번호가 포함됩니다. 응답에서 빼는 일을 매번 잊지 않는 것보다, 애초에 나갈 수 있는 필드만 가진 타입을 두는 쪽이 안전합니다.

**2. 양방향 연관관계가 순환 참조를 만듭니다.** `User.status`와 `UserStatus.user`가 서로를 가리키므로, 직렬화가 둘 사이를 무한히 오갑니다. `@JsonIgnore`로 끊을 수도 있지만, 그러면 JSON 표현을 위한 어노테이션이 도메인 모델에 쌓입니다.

**3. OSIV를 끄면 지연 로딩이 응답 시점에 터집니다.** 이 프로젝트는 `open-in-view: false`입니다. 엔티티를 그대로 반환하면 직렬화가 트랜잭션 밖에서 일어나 `LazyInitializationException`이 납니다. 실제로 이번에 조회 메서드에 트랜잭션이 없어 같은 문제를 만났고, "서비스는 엔티티가 아니라 결과 객체를 반환한다"는 규칙을 세워 구조적으로 막았습니다.

**4. Entity와 API가 결합됩니다.** 이번에 `BinaryContent`에서 `bytes`를 떼어내고 실제 파일을 스토리지로 옮겼는데, 엔티티를 그대로 노출했다면 API 응답 형태가 같이 바뀌었을 겁니다. DTO가 사이에 있어서 저장 방식은 바꾸면서 응답은 스펙대로 유지할 수 있었습니다. 반대 방향도 마찬가지로, 스펙이 `profileId` 대신 프로필 메타 정보를 요구했을 때 엔티티는 건드리지 않았습니다.

**5. 조회 전략을 응답 모양이 정하게 됩니다.** 응답에 무엇이 필요한지가 DTO에 드러나 있으니, 그에 맞춰 `@EntityGraph`나 배치 조회를 붙일 지점이 분명해집니다. 채널 목록의 참여자를 한 번에 모아 오는 것도 "응답에 참여자가 들어간다"는 사실이 DTO에 적혀 있어서 정할 수 있었습니다.

---

## 확인한 것

- 통합 테스트 13개를 포함해 전체 테스트가 실제 PostgreSQL에서 통과합니다.
- 애플리케이션이 만든 OpenAPI 문서의 `UserDto`·`BinaryContentDto`·`ChannelDto`·`MessageDto`·`ReadStatusDto`·`UserStatusDto`가 제공된 API 스펙 v1.1과 필드 단위로 일치합니다. `PageResponse`만 제네릭 구체화 때문에 이름이 `PageResponseMessageDto`이고 필드는 같습니다.
