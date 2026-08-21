# Railway 배포

## 준비된 것

| 파일 | 역할 |
| --- | --- |
| `Dockerfile` | Java 17로 빌드하고 JRE 이미지로 실행 |
| `.dockerignore` | 빌드 산출물과 로컬 데이터를 이미지에서 뺀다 |
| `railway.json` | Nixpacks 대신 Dockerfile로 빌드하도록 지정 |
| `application.yml` | `server.port: ${PORT:8080}` |

Nixpacks에 맡기지 않고 Dockerfile을 쓰는 이유는 JDK 버전 때문이다. 이 프로젝트는
Java 17 toolchain을 쓰는데, Nixpacks가 고르는 JDK가 바뀌면 빌드가 조용히 깨진다.

`PORT` 바인딩은 빠뜨리기 쉽다. Railway는 사용할 포트를 환경 변수로 알려주고 그 포트로
헬스 체크를 한다. 8080에 고정해두면 컨테이너는 떴는데 헬스 체크가 닿지 못해 배포가
실패한다.

## 순서

1. **GitHub에 푸시.** Railway는 레포지토리를 보고 빌드한다.

2. **railway.app 가입** 후 New Project → Deploy from GitHub repo → 이 레포 선택.
   `railway.json`이 있으므로 빌드 방식은 따로 고를 것이 없다.

3. **도메인 생성.** Settings → Networking → Generate Domain.
   포트를 물으면 비워두거나 8080을 넣는다. 실제 값은 Railway가 `PORT`로 넘긴다.

4. **볼륨 연결** (선택이지만 권장). 이 단계를 건너뛰면 **배포할 때마다 데이터가 사라진다.**
   컨테이너 파일 시스템은 재배포와 함께 없어지기 때문이다.

   Settings → Volumes → New Volume, Mount path를 `/app/data`로 지정한다.
   Dockerfile이 `DISCODEIT_DATA_ROOT=/app/data`를 이미 가리키고 있어 추가 설정은 없다.

5. **확인.** 생성된 도메인에서 아래가 모두 열려야 한다.

   ```
   https://<도메인>/                  프론트엔드 화면
   https://<도메인>/swagger-ui.html   Swagger UI
   https://<도메인>/v3/api-docs       OpenAPI 문서
   ```

   Postman 컬렉션의 `baseUrl`을 이 도메인으로 바꾸면 배포된 서버를 그대로 테스트할 수 있다.

## 환경 변수

기본값이 있어 아무것도 넣지 않아도 뜬다. 바꿀 일이 있을 때만 설정한다.

| 이름 | 기본값 | 설명 |
| --- | --- | --- |
| `PORT` | `8080` | Railway가 넣어준다. 직접 설정하지 않는다 |
| `DISCODEIT_DATA_ROOT` | `/app/data` | 파일 저장소 경로. 볼륨 마운트 위치와 맞춘다 |
| `DISCODEIT_REPOSITORY_TYPE` | `file` | `jcf`로 두면 메모리에만 저장한다 |

## 잘 안 될 때

**헬스 체크 실패 / 도메인이 502** — `PORT`를 읽고 있는지 본다. 배포 로그에
`Tomcat started on port 8080`만 찍히고 Railway가 다른 포트를 기대하는 상황이다.

**재배포하면 데이터가 사라짐** — 볼륨을 붙이지 않았거나 마운트 경로가
`DISCODEIT_DATA_ROOT`와 다르다.

**빌드가 gradlew 권한으로 실패** — Dockerfile이 `chmod +x gradlew`를 하므로 보통은
문제가 없다. 그래도 난다면 레포의 `gradlew` 실행 권한이 지워진 것이므로
`git update-index --chmod=+x gradlew`로 되돌린다.
