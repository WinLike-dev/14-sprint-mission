package com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

/**
 * 비공개 채널 생성 요청 DTO.
 *
 * 누락을 생성자에서 터뜨리지 않고 @NotEmpty로 판단한다.
 * 역직렬화 중 NPE가 나면 어떤 필드가 문제인지 알려주지 못하기 때문이다.
 */
public record PrivateChannelCreateRequest(
        @NotEmpty(message = "participantIds는 비어 있을 수 없습니다.")
        List<UUID> participantIds
) {
    public PrivateChannelCreateRequest {
        participantIds = participantIds == null ? null : List.copyOf(participantIds);
    }
}
