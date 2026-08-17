package com.sprint.mission.discodeit.channel.application.port.out;

import java.util.UUID;

/**
 * 채널 모듈이 사용자 존재 여부를 확인할 때 쓰는 outbound 포트.
 * 구현은 adapter.out의 ChannelUserAcl이 user 모듈 API를 호출한다.
 */
public interface ChannelUserReader {

    // 사용자가 존재하는지 확인하고, 없으면 예외를 던진다
    void requireExists(UUID userId);
}
