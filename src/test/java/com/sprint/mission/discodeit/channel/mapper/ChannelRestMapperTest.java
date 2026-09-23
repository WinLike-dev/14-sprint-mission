package com.sprint.mission.discodeit.channel.mapper;

import com.sprint.mission.discodeit.channel.controller.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.controller.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.controller.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.controller.dto.response.ChannelDto;
import com.sprint.mission.discodeit.channel.service.dto.result.ChannelResult;
import com.sprint.mission.discodeit.channel.service.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.command.UpdatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.content.service.dto.result.BinaryContentResult;
import com.sprint.mission.discodeit.user.service.dto.result.UserResult;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelRestMapperTest {

    private final ChannelRestMapper mapper = Mappers.getMapper(ChannelRestMapper.class);

    @Test
    void mapsPublicCreateRequestToCommand() {
        PublicChannelCreateRequest request =
                new PublicChannelCreateRequest("general", "공개 채널");

        CreatePublicChannelCommand command = mapper.toCommand(request);

        assertThat(command.name()).isEqualTo("general");
        assertThat(command.description()).isEqualTo("공개 채널");
    }

    @Test
    void mapsPrivateCreateRequestToCommand() {
        UUID participantId = UUID.randomUUID();
        PrivateChannelCreateRequest request =
                new PrivateChannelCreateRequest(List.of(participantId));

        CreatePrivateChannelCommand command = mapper.toCommand(request);

        assertThat(command.participantIds()).containsExactly(participantId);
    }

    @Test
    void mapsUpdateRequestToCommand() {
        PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("new-name", "new-description");

        UpdatePublicChannelCommand command = mapper.toCommand(request);

        assertThat(command.newName()).isEqualTo("new-name");
        assertThat(command.newDescription()).isEqualTo("new-description");
    }

    // 참여자와 그 프로필처럼 중첩된 결과까지 응답 DTO로 옮겨지는지 본다.
    @Test
    void mapsResultToResponse() {
        UUID channelId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        Instant lastMessageAt = Instant.now();
        UserResult participant = new UserResult(
                participantId,
                "participant",
                "participant@example.com",
                new BinaryContentResult(profileId, "profile.png", 3L, "image/png"),
                true
        );
        ChannelResult result = new ChannelResult(
                channelId,
                ChannelType.PRIVATE,
                null,
                null,
                List.of(participant),
                lastMessageAt
        );

        ChannelDto response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(channelId);
        assertThat(response.type()).isEqualTo(ChannelType.PRIVATE);
        assertThat(response.lastMessageAt()).isEqualTo(lastMessageAt);
        assertThat(response.participants()).hasSize(1);
        assertThat(response.participants().get(0).id()).isEqualTo(participantId);
        assertThat(response.participants().get(0).online()).isTrue();
        assertThat(response.participants().get(0).profile().fileName()).isEqualTo("profile.png");
    }
}
