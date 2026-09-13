package com.sprint.mission.discodeit.channel.adapter.in.rest.channel;

import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.response.ChannelDto;
import com.sprint.mission.discodeit.channel.application.channel.dto.ChannelResult;
import com.sprint.mission.discodeit.channel.application.channel.dto.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.channel.application.channel.dto.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.application.channel.dto.UpdatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.domain.channel.ChannelType;
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

    @Test
    void mapsResultToResponse() {
        UUID channelId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        Instant lastMessageAt = Instant.now();
        ChannelResult result = new ChannelResult(
                channelId,
                ChannelType.PRIVATE,
                null,
                null,
                List.of(participantId),
                lastMessageAt
        );

        ChannelDto response = mapper.toResponse(result);

        assertThat(response.id()).isEqualTo(channelId);
        assertThat(response.type()).isEqualTo(ChannelType.PRIVATE);
        assertThat(response.participantIds()).containsExactly(participantId);
        assertThat(response.lastMessageAt()).isEqualTo(lastMessageAt);
    }
}
