package com.sprint.mission.discodeit.channel.service.dto.result;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.user.service.dto.result.UserResult;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record ChannelResult(
        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UserResult> participants,
        Instant lastMessageAt
) {

    public ChannelResult {
        participants = List.copyOf(
                Objects.requireNonNull(
                        participants,
                        "participants는 null일 수 없습니다."
                )
        );
    }

    // 참여자와 lastMessageAt은 채널이 아니라 읽음 상태와 메시지에서 구하는 값이라 호출자가 넘긴다.
    // 메시지가 없으면 lastMessageAt은 null이다.
    public static ChannelResult from(Channel channel, List<UserResult> participants, Instant lastMessageAt) {
        Objects.requireNonNull(channel, "channel은 null일 수 없습니다.");

        return new ChannelResult(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participants,
                lastMessageAt
        );
    }
}
