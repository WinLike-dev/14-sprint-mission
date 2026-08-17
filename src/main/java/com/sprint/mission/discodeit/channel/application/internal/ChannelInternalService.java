package com.sprint.mission.discodeit.channel.application.internal;

import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;
import com.sprint.mission.discodeit.channel.api.ChannelInternalApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * ChannelInternalApi의 구현체.
 * 다른 모듈은 ChannelRepository가 아니라 이 서비스를 통해 채널 존재 여부만 확인한다.
 */
@Service
@RequiredArgsConstructor
public class ChannelInternalService implements ChannelInternalApi {

    private final ChannelRepository channelRepository;

    // 채널이 존재하는지 확인하고, 없으면 예외를 던진다 (getById 내부에서 처리)
    @Override
    public void requireExists(UUID channelId) {
        channelRepository.getById(channelId);
    }
}
