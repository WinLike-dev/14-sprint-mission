package com.sprint.mission.discodeit.channel.application.channel;

import com.sprint.mission.discodeit.channel.domain.channel.Channel;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * 채널 lastMessageAt 갱신 유스케이스.
 * inbound 이벤트 핸들러(ChannelMessageChangedEventHandler)가 호출한다.
 */
@Service
@RequiredArgsConstructor
public class ChannelMessageActivityService {

    private final ChannelRepository channelRepository;

    // 채널의 마지막 메시지 시각을 갱신하고 저장소에 반영
    public void updateLastMessageAt(UUID channelId, Instant lastMessageAt) {
        Channel channel = channelRepository.getById(channelId);
        channel.updateLastMessageAt(lastMessageAt);
        channelRepository.update(channel);
    }
}
