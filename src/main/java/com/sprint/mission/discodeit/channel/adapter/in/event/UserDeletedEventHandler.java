package com.sprint.mission.discodeit.channel.adapter.in.event;

import com.sprint.mission.discodeit.channel.application.readstatus.ReadStatusCleanupService;
import com.sprint.mission.discodeit.user.api.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * user 모듈이 발행한 UserDeletedEvent를 받는 inbound 어댑터.
 * 표현 계층에서 이벤트를 수신한 뒤 ReadStatusCleanupService에 정리만 위임한다.
 */
@Component
@RequiredArgsConstructor
public class UserDeletedEventHandler {

    private final ReadStatusCleanupService cleanupService; // 읽음 상태 정리 서비스

    // Spring의 @EventListener로 UserDeletedEvent를 수신하여 처리
    @EventListener
    public void handle(UserDeletedEvent event) {
        // 유저 삭제 시 ReadStatus 삭제
        cleanupService.deleteAllByUserId(event.userId());
    }
}
