package com.sprint.mission.discodeit.channel.adapter.in.event;

import com.sprint.mission.discodeit.channel.application.channel.ChannelMessageActivityService;
import com.sprint.mission.discodeit.message.api.event.ChannelMessageChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * message 모듈이 발행한 ChannelMessageChangedEvent를 받는 inbound 어댑터.
 * 표현 계층에서 이벤트를 수신한 뒤 ChannelMessageActivityService에 갱신만 위임한다.
 */
@Component
@RequiredArgsConstructor
public class ChannelMessageChangedEventHandler {

    private final ChannelMessageActivityService activityService; // 채널 메시지 활동 서비스

    // Spring의 @EventListener로 ChannelMessageChangedEvent를 수신하여 처리
    @EventListener
    public void handle(ChannelMessageChangedEvent event) {
        // 가장 최신 메세지의 시각을 채널에 넣어주기
        activityService.updateLastMessageAt(event.channelId(), event.lastMessageAt());
    }
}
