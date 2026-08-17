package com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.response;

import com.sprint.mission.discodeit.channel.domain.channel.Channel;
import com.sprint.mission.discodeit.channel.domain.channel.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 채널 응답 DTO.
 * 채널 정보를 클라이언트에게 반환할 때 사용하는 객체이다.
 * 도메인 엔티티(Channel)를 직접 노출하지 않고, 필요한 정보만 담아서 반환한다.
 */
public record ChannelDto(
        UUID id,              // 채널 고유 ID
        Instant createdAt,    // 채널 생성 시각
        Instant updatedAt,    // 채널 마지막 수정 시각
        ChannelType type,     // 채널 유형 (PUBLIC / PRIVATE)
        String name,          // 채널 이름 (PRIVATE은 null)
        String description,   // 채널 설명 (PRIVATE은 null)
        Instant lastMessageAt, // 마지막 메시지 시각
        List<UUID> participantIds // 참여자 ID 목록 (PUBLIC은 빈 리스트)
) {
    // 참여자 목록을 불변 리스트로 복사하여, 외부에서 수정하는 것을 방지
    public ChannelDto {
        participantIds = List.copyOf(participantIds);
    }

    // Channel 엔티티와 참여자 목록을 받아서 ChannelDto로 변환하는 팩토리 메서드
    public static ChannelDto from(
            Channel channel,
            List<UUID> participantIds
    ) {
        return new ChannelDto(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getLastMessageAt(),
                participantIds
        );
    }
}
