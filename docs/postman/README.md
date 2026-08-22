# Postman

`discodeit.postman_collection.json`은 API 20개를 전부 훑는다. 폴더 순서대로 실행하면
앞 요청이 만든 ID를 뒤 요청이 이어받으므로, 값을 손으로 채울 필요가 없다.

| 파일 | 내용 |
| --- | --- |
| `discodeit.postman_collection.json` | 컬렉션 (요청 32건, 단언 66건) |
| `newman-run-output.txt` | 실행 결과 (사람이 읽는 형태) |
| `newman-run-report.json` | 실행 결과 (요청·응답 전문 포함) |
| `sample-attachment.png` | 첨부 테스트용 1x1 PNG |

## 실행

Postman 앱에서는 컬렉션을 Import 한 뒤 컬렉션 변수 `baseUrl`을 서버 주소로 바꾸고
Runner로 돌린다. 파일을 담는 두 요청은 앱이 로컬 경로를 다시 묻는다.
`sample-attachment.png`를 고르면 된다.

명령줄에서는 newman으로 돌린다. 이때는 파일 경로를 직접 찾아가므로 되묻지 않는다.

```
cd docs/postman
npx newman run discodeit.postman_collection.json \
  --env-var baseUrl=http://localhost:8080 \
  --working-dir .
```

## 무엇을 확인하는가

상태 코드만 보지 않는다. 응답이 실제로 그 일을 했는지까지 확인한다.

- **multipart** — 프로필을 붙여 등록하면 `profileId`가 생기고, 파트를 아예 보내지
  않으면 `null`로 남는다. 프론트엔드는 파일이 없을 때 파트를 보내지 않으므로
  두 경우가 모두 성공해야 한다.
- **부분 수정** — `newUsername`만 보내면 username만 바뀌고 email은 그대로다.
- **시각의 주인** — `lastReadAt`과 `newLastActiveAt`은 보낸 값이 그대로 저장된다.
  서버 수신 시각으로 덮어쓰지 않는다.
- **id로 지정** — 읽음 상태 목록의 모든 항목이 `id`를 가진다. 프론트엔드가 이 값을
  들고 있다가 갱신에 쓴다.
- **응답에 password가 없다** — 등록과 로그인 응답 모두.
- **실패 응답** — 문서에 적힌 상태 코드가 실제로 나오는지 본다.
  400(검증), 401(로그인 실패), 404(없는 리소스), 409(중복·상태 충돌).
  검증 실패는 어떤 필드가 왜 틀렸는지 `fieldErrors`로 하나씩 돌려준다.

`7. 실패 응답` 폴더의 로그인 두 건은 짚어둘 만하다. 없는 username과 틀린 password가
모두 401이다. 둘을 나누면 어떤 username이 등록되어 있는지 알려주는 셈이 된다.

## 다시 돌릴 때

컬렉션은 실행할 때마다 `runId`를 새로 만들어 username에 붙인다. 같은 서버에 여러 번
돌려도 중복으로 막히지 않는다. `8. 정리` 폴더가 만든 것을 모두 지우므로 데이터도 남지
않는다.
