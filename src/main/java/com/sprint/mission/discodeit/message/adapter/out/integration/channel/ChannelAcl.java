package com.sprint.mission.discodeit.message.adapter.out.integration.channel;

import com.sprint.mission.discodeit.channel.api.ChannelInternalApi;
import com.sprint.mission.discodeit.message.application.port.out.MessageChannelReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * MessageChannelReader outbound 포트의 어댑터.
 * channel 모듈의 노출 API를 호출하고, 메시지 모듈이 channel 구현에 직접 의존하지 않게 막는다.
 */
@Component
@RequiredArgsConstructor
public class ChannelAcl implements MessageChannelReader {

    private final ChannelInternalApi channelInternalApi; // 채널 모듈이 제공하는 내부 API

    // 채널 모듈의 내부 API를 호출하여 채널 존재 여부를 확인한다
    @Override
    public void requireExists(UUID channelId) {
        channelInternalApi.requireExists(channelId);
    }
}
