package com.sprint.mission.discodeit.channel.adapter.out.integration.user;

import com.sprint.mission.discodeit.channel.application.port.out.ChannelUserReader;
import com.sprint.mission.discodeit.user.api.UserInternalApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ChannelUserReader outbound 포트의 어댑터.
 * user 모듈의 노출 API를 호출하고, 채널 모듈이 user 구현에 직접 의존하지 않게 막는다.
 */
@Component
@RequiredArgsConstructor
public class ChannelUserAcl implements ChannelUserReader {

    private final UserInternalApi userInternalApi; // 사용자 모듈이 외부에 제공하는 내부 API

    // 사용자 존재 여부 확인을 사용자 모듈의 내부 API에 위임
    @Override
    public void requireExists(UUID userId) {
        userInternalApi.requireExists(userId);
    }
}
