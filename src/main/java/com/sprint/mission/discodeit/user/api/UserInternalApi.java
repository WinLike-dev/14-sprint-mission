package com.sprint.mission.discodeit.user.api;

import java.util.UUID;

/**
 * user 모듈이 다른 모듈에게 노출하는 동기 API.
 * channel, message 등이 사용자 존재 여부를 확인할 때 이 계약을 사용한다.
 * REST가 아니라 모듈 간 호출용이며, 구현은 UserInternalService가 담당한다.
 */
public interface UserInternalApi {

    // 해당 userId의 사용자가 존재하는지 확인한다. 존재하지 않으면 예외를 던진다.
    void requireExists(UUID userId);
}
