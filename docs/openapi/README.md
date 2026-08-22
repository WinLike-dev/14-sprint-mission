# OpenAPI

| 파일 | 내용 |
| --- | --- |
| `discodeit-api-docs.json` | springdoc이 우리 코드에서 뽑아낸 문서 |
| `provided-api-docs.json` | 미션이 제공한 스펙 |
| `oasdiff-report.txt` | 둘의 차이 |

실행 중인 서버에서는 `/v3/api-docs`가 같은 문서를 내려주고, `/swagger-ui.html`에서
직접 호출해볼 수 있다.

## 차이 읽는 법

```
### New Endpoints: 2
### Deleted Endpoints: 3
### Modified Endpoints: 14
```

읽음 상태를 뺀 나머지 18개는 경로와 메서드가 스펙과 같다. 신규·삭제로 잡힌 다섯 건은
모두 읽음 상태 하나에서 나온 것이다.

**하나. 읽음 상태의 주소.** 스펙은 읽음 상태를 저장용 id로 지정한다.

```
POST  /api/readStatuses                    → 생성
PATCH /api/readStatuses/{readStatusId}     → 수정
```

그런데 `GET /api/readStatuses/{readStatusId}`는 스펙에 없다. id로 주소를 지정하라면서
id를 얻는 길은 주지 않는다. 클라이언트는 목록을 받아 캐시해 두어야만 수정할 수 있고,
캐시가 어긋나면 이미 있는 것을 또 만들려다 거절당한다.

읽음 상태의 정체는 (userId, channelId) 조합이다. 한 사용자는 한 채널에 대해 하나만
가지므로 그 조합만으로 대상이 정해진다. 저장소도 그 조합으로 찾는다. 그래서 조합을
그대로 주소로 쓴다.

```
PUT    /api/readStatuses/{userId}/{channelId}   → 없으면 만들고 있으면 갱신
GET    /api/readStatuses/{userId}/{channelId}   → 단건 조회
DELETE /api/readStatuses/{userId}/{channelId}   → 삭제
GET    /api/readStatuses?userId={userId}        → 목록 (스펙과 같다)
```

같은 요청을 몇 번 보내도 결과가 같다. 클라이언트는 채널을 열 때 이미 channelId를 알고
있으므로 id를 보관할 이유도, 생성과 수정을 갈라 부를 이유도 없다. 프론트엔드가 들고
있던 `있으면 PATCH, 없으면 POST` 분기가 이 변경으로 사라졌다.

중복 등록이라는 실패도 함께 사라졌다. 조합이 유일하다는 사실은 서버가 이미 아는 것이라
서버가 흡수한다. 남은 409는 비공개 채널에 읽음 상태를 **새로** 만들려는 경우 하나뿐이다.
비공개 채널의 읽음 상태는 채널을 만들 때 참여자 몫이 함께 생기므로, 참여자가 읽은 시각을
갱신하는 요청은 그대로 성공한다.

**둘. 상태 코드.** 스펙은 중복 등록과 Private Channel 수정을 400으로, 로그인 실패를
400과 404로 나눠 적는다. 우리는 그렇게 응답하지 않는다.

| 상황 | 스펙 | 우리 | 이유 |
| --- | --- | --- | --- |
| username·email 중복 | 400 | 409 | 요청은 올바르다. 저장된 데이터와 충돌할 뿐이다 |
| Private Channel 수정 | 400 | 409 | 같은 요청이 공개 채널에서는 성공한다 |
| 로그인 실패 | 400 / 404 | 401 | 없는 username과 틀린 password를 구분해 알리지 않는다 |

400은 요청을 이해하지 못했다는 뜻이라 원인을 잘못 가리킨다. 문서를 스펙에 맞추면
그 순간 문서가 거짓이 되므로 우리 구현을 따랐다. 프론트엔드는 상태 코드로 분기하지
않으므로(`status===200` 한 곳뿐) 연동에는 영향이 없다.

**셋. 응답 DTO.** 스펙은 같은 리소스인데 엔드포인트마다 응답 타입이 다르다. 목록은
DTO를, 생성과 수정은 엔티티를 돌려준다. 그러면 클라이언트가 한 리소스에 대해 두 가지
모양을 다뤄야 하고, 사용자 생성 응답에는 password 평문이 실린다.

  `GET /api/users` → UserDto · `POST /api/users` → User(password 포함)
  `GET /api/channels` → ChannelDto · `PATCH /api/channels/{id}` → Channel

전부 DTO로 통일했다. 그래서 `password`가 없고 `online`이 있다는 차이가 남는다.

**넷. 검증 제약.** `required`, `maxLength`, `minLength`는 우리 문서에만 있다. 요청 DTO에
붙인 `@NotBlank`, `@Size`를 springdoc이 읽어 넣은 것이라 지울 이유가 없다. 이쪽 문서가
더 정확하다.

## 다시 뽑기

```
curl -s http://localhost:8080/v3/api-docs > docs/openapi/discodeit-api-docs.json
oasdiff diff docs/openapi/discodeit-api-docs.json docs/openapi/provided-api-docs.json -f text
```
