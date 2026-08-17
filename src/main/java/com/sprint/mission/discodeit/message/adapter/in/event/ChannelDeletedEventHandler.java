package com.sprint.mission.discodeit.message.adapter.in.event;

import com.sprint.mission.discodeit.channel.api.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.message.application.message.MessageCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * channel 모듈이 발행한 ChannelDeletedEvent를 받는 inbound 어댑터.
 * 표현 계층에서 이벤트를 수신한 뒤 MessageCleanupService에 정리만 위임한다.
 */
@Component
@RequiredArgsConstructor
public class ChannelDeletedEventHandler {

    private final MessageCleanupService cleanupService;

    // 채널 삭제 이벤트가 발생하면 해당 채널의 모든 메시지를 삭제한다
    @EventListener
    public void handle(ChannelDeletedEvent event) {
        cleanupService.deleteAllByChannelId(event.channelId());
    }
}
