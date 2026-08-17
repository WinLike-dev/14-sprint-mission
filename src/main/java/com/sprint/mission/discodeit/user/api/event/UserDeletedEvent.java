package com.sprint.mission.discodeit.user.api.event;

import java.util.UUID;

/**
 * user 모듈이 발행하는 사용자 삭제 이벤트.
 * UserServiceImpl이 삭제 후 Events.raise로 올리고,
 * 채널 모듈의 inbound 핸들러가 구독해 읽음 상태를 정리한다.
 */
public record UserDeletedEvent(UUID userId) {
}
