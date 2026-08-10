# 002 스타일 재리뷰

- 재리뷰 일자: 2026-07-24
- 이전 리뷰: `reviews/001_review.md`
- 수정 요청 범위: `R-001`, `R-002`
- 전체 판정: 요청 범위 해결

## 이전 발견 재검증

### R-001 해결됨

- `UserService#createUser`의 첫 파라미터를 `username`으로 변경했다.
- `JCFUserService#createUser`, `FileUserService#createUser`, `BasicUserService#createUser`가 모두 `username`을 사용한다.
- `User#username`, 생성자, `update`와 같은 도메인 용어로 통일됐다.

### R-002 해결됨

- 세 서비스 인터페이스가 메서드 사이에 빈 줄을 사용한다.
- Basic 서비스의 100자 이하 메서드 선언과 짧은 대입식을 한 줄로 정리했다.
- `JavaApplication`의 긴 호출은 인자별로 줄바꿈했다.
- `JavaApplication`의 예외 변수 이름을 `e`에서 `exception`으로 변경했다.
- 검사한 Java 소스에서 100자 초과 줄, 탭, 후행 공백을 발견하지 못했다.

### R-003 미해결 — 요청 범위 제외

- 사용자가 `R-001`, `R-002`만 적용하도록 지정했다.
- File 구현의 `directory` 인스턴스 필드는 변경하지 않았다.

## 회귀 확인

- 새 Blocker, Major, Minor 발견 없음
- `git diff --check`: 통과
- `./gradlew clean test`: 성공 (`test NO-SOURCE`)
- `JavaApplication` JCF 모드: CRUD, 관계 검증, 싱글톤 확인 성공
- `JavaApplication file` 모드: CRUD, 관계 검증, 서비스 재생성 후 Message 조회 성공

## 결론

- 요청한 이름 및 포맷 통일 작업은 완료됐다.
- `R-003`은 의도적으로 현재 구조를 유지한다.
