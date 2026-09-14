# Discodeit

Java 17 · Spring Boot 3.4.0 · Gradle 8.11.1 프로젝트입니다.
Spring Boot는 로컬에서 실행하고, Docker Compose는 PostgreSQL 17 서버만 실행합니다.

현재 앱은 기본적으로 `data/`의 파일 저장소를 사용합니다. DB 컨테이너는 향후 DB 연동을 위한 환경이며,
아직 앱에 DB 드라이버·JPA/JDBC·DB 저장소 구현이 없어 DB를 시작해도 앱 데이터가 DB에 저장되지는 않습니다.

## 준비

- Java 17
- Docker와 Docker Compose v2
- 프로젝트 루트의 `.env` (Git 제외)

새로 내려받은 프로젝트에서는 예시 파일을 복사합니다. 기존 `.env`가 있다면 필요한 값만 직접 수정합니다.

```bash
cp -n .env.example .env
```

`.env.example`의 계정과 비밀번호는 로컬 개발용 예시입니다.
Compose는 `.env`를 읽어 DB 컨테이너 설정에 사용합니다. 로컬 Spring Boot에 자동으로 전달되는 설정은 아닙니다.

## DB 실행과 접속

```bash
docker compose up -d --wait
docker compose ps
```

| 항목 | 기본값 |
| --- | --- |
| 호스트 | `127.0.0.1` |
| 포트 | `5432` (`DB_PORT`로 변경 가능) |
| DB 이름 | `discodeit` (`DB_NAME`) |
| 사용자 | `discodeit` (`DB_USER`) |
| 비밀번호 | `.env`의 `DB_PASSWORD` |
| 향후 앱 연결용 JDBC URL | `jdbc:postgresql://127.0.0.1:5432/discodeit` |

DB는 로컬 PC에서만 접속하도록 포트를 연결합니다. 컨테이너 내부에서는 항상 5432 포트를 사용합니다.

```bash
docker compose exec db sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"'
```

종료하려면 다음 명령을 사용합니다. DB 데이터는 named volume에 보존됩니다.

```bash
docker compose down
```

`docker compose down -v`는 DB 데이터 볼륨까지 삭제하므로 데이터 초기화가 필요할 때만 사용합니다.
초기 계정·DB 설정은 빈 볼륨으로 처음 시작할 때 적용됩니다. 기존 DB의 비밀번호는 `.env` 변경만으로 바뀌지 않습니다.
설정 기준: [PostgreSQL 공식 이미지 문서](https://github.com/docker-library/docs/blob/master/postgres/README.md).

## Spring Boot 실행

프로젝트 루트에서 실행합니다. 데이터는 PostgreSQL에 저장하므로 DB가 먼저 실행 중이어야 합니다.

```bash
./gradlew bootRun
```

| 기능 | 주소 |
| --- | --- |
| 프론트 화면 | http://localhost:8080/ |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

앱 포트는 Spring Boot 기본값인 8080입니다. 변경하려면 실행 환경에 `SERVER_PORT`를 지정합니다.
파일 업로드 제한은 파일당 10MB, 요청당 30MB입니다.

## 빌드와 테스트

```bash
./gradlew build
```

`build`는 테스트와 JAR 생성을 수행합니다. 테스트만 실행할 때는 `./gradlew test`를 사용합니다.

## 프로젝트 구조

```text
src/main/java/          애플리케이션 코드
src/main/resources/    앱 설정과 프론트 정적 파일
src/test/java/          테스트
docs/openapi/           API 명세와 비교 자료
docs/postman/           API 호출·검증 자료
compose.yml            DB 서버 실행 설정
.env.example           로컬 DB 환경 변수 예시
```
