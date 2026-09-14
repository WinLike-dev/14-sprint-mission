package com.sprint.mission.discodeit.message.mapper;

import com.sprint.mission.discodeit.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.service.dto.CreateMessageCommand;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageRestMapperTest {

    private final MessageRestMapper mapper = Mappers.getMapper(MessageRestMapper.class);

    @Test
    void ignoresEmptyAttachmentsAndConvertsFilesWithContent() {
        MessageCreateRequest request = new MessageCreateRequest(
                "hello",
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        MockMultipartFile empty = new MockMultipartFile(
                "attachments",
                "empty.txt",
                "text/plain",
                new byte[0]
        );
        MockMultipartFile content = new MockMultipartFile(
                "attachments",
                "message.txt",
                "text/plain",
                new byte[]{4, 5}
        );

        CreateMessageCommand command = mapper.toCommand(
                request,
                List.of(empty, content)
        );

        assertThat(command.attachments()).hasSize(1);
        assertThat(command.attachments().get(0).fileName()).isEqualTo("message.txt");
        assertThat(command.attachments().get(0).bytes()).containsExactly(4, 5);
    }

    @Test
    void mapsMissingAttachmentsToEmptyList() {
        MessageCreateRequest request = new MessageCreateRequest(
                "hello",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        CreateMessageCommand command = mapper.toCommand(request, null);

        assertThat(command.attachments()).isEmpty();
    }
}
