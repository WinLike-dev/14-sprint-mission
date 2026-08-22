package com.sprint.mission.discodeit.content.api;

import java.util.UUID;

/**
 * content 모듈이 다른 모듈에게 노출하는 동기 API.
 * REST로는 조회만 열고, 생성/삭제는 이 계약을 통해서만 받는다.
 * 구현은 ContentInternalService가 담당한다.
 */
public interface ContentInternalApi {

    // 바이너리 콘텐츠를 생성하고 생성된 ID를 반환한다
    UUID create(BinaryContentPayload payload);

    // ID로 바이너리 콘텐츠를 삭제한다
    void delete(UUID contentId);
}
