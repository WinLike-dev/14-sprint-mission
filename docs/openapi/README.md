# OpenAPI

| 파일 | 내용 |
| --- | --- |
| `discodeit-api-docs.json` | springdoc이 우리 코드에서 뽑아낸 문서 |
| `provided-api-docs.json` | 미션이 제공한 스펙 |
| `oasdiff-report.txt` | 둘의 차이 |

실행 중인 서버에서는 `/v3/api-docs`가 같은 문서를 내려주고, `/swagger-ui.html`에서
직접 호출해볼 수 있다.

## 차이 읽는 법

경로와 메서드는 완전히 같다. 신규 0, 삭제 0.

```
### New Endpoints: None
### Deleted Endpoints: None
### Modified Endpoints: 16
```

남은 16건은 모두 의도한 차이다.

**하나. 상태 코드.** 스펙은 중복 등록과 Private Channel 수정을 400으로, 로그인 실패를
400과 404로 나눠 적는다. 우리는 그렇게 응답하지 않는다.

| 상황 | 스펙 | 우리 | 이유 |
| --- | --- | --- | --- |
| username·email 중복 | 400 | 409 | 요청은 올바르다. 저장된 데이터와 충돌할 뿐이다 |
| Private Channel 수정 | 400 | 409 | 같은 요청이 공개 채널에서는 성공한다 |
| 이미 있는 읽음 상태 | 400 | 409 | 위와 같다 |
| 로그인 실패 | 400 / 404 | 401 | 없는 username과 틀린 password를 구분해 알리지 않는다 |

400은 요청을 이해하지 못했다는 뜻이라 원인을 잘못 가리킨다. 문서를 스펙에 맞추면
그 순간 문서가 거짓이 되므로 우리 구현을 따랐다. 프론트엔드는 상태 코드로 분기하지
않으므로(`status===200` 한 곳뿐) 연동에는 영향이 없다.

**둘. 응답 DTO.** 스펙은 같은 리소스인데 엔드포인트마다 응답 타입이 다르다. 목록은
DTO를, 생성과 수정은 엔티티를 돌려준다. 그러면 클라이언트가 한 리소스에 대해 두 가지
모양을 다뤄야 하고, 사용자 생성 응답에는 password 평문이 실린다.

  `GET /api/users` → UserDto · `POST /api/users` → User(password 포함)
  `GET /api/channels` → ChannelDto · `PATCH /api/channels/{id}` → Channel

전부 DTO로 통일했다. 그래서 `password`가 없고 `online`이 있다는 차이가 남는다.

**셋. 검증 제약.** `required`, `maxLength`, `minLength`는 우리 문서에만 있다. 요청 DTO에
붙인 `@NotBlank`, `@Size`를 springdoc이 읽어 넣은 것이라 지울 이유가 없다. 이쪽 문서가
더 정확하다.

## 다시 뽑기

```
curl -s http://localhost:8080/v3/api-docs > docs/openapi/discodeit-api-docs.json
oasdiff diff docs/openapi/discodeit-api-docs.json docs/openapi/provided-api-docs.json -f text
```
