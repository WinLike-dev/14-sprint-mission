package com.sprint.mission.discodeit.message.api.event;

import java.time.Instant;
import java.util.UUID;

/**
 * 메시지 모듈이 발행하는 채널 메시지 변경 이벤트.
 * MessageServiceImpl이 생성/삭제 후 Events.raise로 올리고,
 * 채널 모듈의 inbound 핸들러가 구독해 lastMessageAt을 갱신한다.
 *
 * @param channelId     메시지가 변경된 채널의 ID
 * @param lastMessageAt 갱신할 마지막 메시지 시각
 */
public record ChannelMessageChangedEvent(UUID channelId, Instant lastMessageAt) {
}
