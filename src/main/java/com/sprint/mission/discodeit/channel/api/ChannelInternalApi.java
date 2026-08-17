package com.sprint.mission.discodeit.channel.api;

import java.util.UUID;

/**
 * 채널 모듈이 다른 모듈에게 노출하는 동기 API.
 * 메시지 모듈 등이 채널 존재 여부를 확인할 때 이 계약을 사용한다.
 * REST가 아니라 모듈 간 호출용이며, 구현은 ChannelInternalService가 담당한다.
 */
public interface ChannelInternalApi {

    // 채널이 존재하는지 확인하고, 없으면 예외를 던진다
    void requireExists(UUID channelId);
}
