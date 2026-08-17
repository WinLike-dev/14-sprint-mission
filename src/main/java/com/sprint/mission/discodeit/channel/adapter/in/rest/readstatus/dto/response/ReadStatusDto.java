package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response;

import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * 읽음 상태 응답 DTO.
 * 읽음 상태 정보를 클라이언트에게 반환할 때 사용하는 객체이다.
 *
 * @param userId     사용자 ID
 * @param channelId  채널 ID
 * @param lastReadAt 마지막으로 읽은 시각
 */
public record ReadStatusDto(UUID userId, UUID channelId, Instant lastReadAt) {

    // ReadStatus 엔티티를 ReadStatusDto로 변환하는 팩토리 메서드
    public static ReadStatusDto from(ReadStatus status) {
        return new ReadStatusDto(status.getUserId(), status.getChannelId(), status.getLastReadAt());
    }
}
