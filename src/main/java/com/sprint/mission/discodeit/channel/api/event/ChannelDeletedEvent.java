package com.sprint.mission.discodeit.channel.api.event;

import java.util.UUID;

/**
 * 채널 모듈이 발행하는 채널 삭제 이벤트.
 * ChannelServiceImpl이 삭제 후 Events.raise로 올리고,
 * 메시지 모듈의 inbound 핸들러가 구독해 관련 메시지를 정리한다.
 *
 * @param channelId 삭제된 채널의 ID
 */
public record ChannelDeletedEvent(UUID channelId) {
}
