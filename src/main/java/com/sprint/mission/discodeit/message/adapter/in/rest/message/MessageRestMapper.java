package com.sprint.mission.discodeit.message.adapter.in.rest.message;

import com.sprint.mission.discodeit.common.exception.UploadedFileReadException;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.response.MessageDto;
import com.sprint.mission.discodeit.message.application.message.dto.CreateMessageCommand;
import com.sprint.mission.discodeit.message.application.message.dto.MessageAttachmentCommand;
import com.sprint.mission.discodeit.message.application.message.dto.MessageResult;
import com.sprint.mission.discodeit.message.application.message.dto.UpdateMessageCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MessageRestMapper {

    @Mapping(target = "attachments", source = "attachments")
    CreateMessageCommand toCommand(
            MessageCreateRequest request,
            List<MultipartFile> attachments
    );

    @Mapping(target = "content", source = "newContent")
    UpdateMessageCommand toCommand(MessageUpdateRequest request);

    MessageDto toResponse(MessageResult result);

    List<MessageDto> toResponses(List<MessageResult> results);

    default List<MessageAttachmentCommand> toAttachmentCommands(
            List<MultipartFile> attachments
    ) {
        if (attachments == null) {
            return List.of();
        }
        return attachments.stream()
                .filter(attachment -> !attachment.isEmpty())
                .map(this::toAttachmentCommand)
                .toList();
    }

    default MessageAttachmentCommand toAttachmentCommand(MultipartFile attachment) {
        try {
            return new MessageAttachmentCommand(
                    attachment.getOriginalFilename(),
                    attachment.getContentType(),
                    attachment.getBytes()
            );
        } catch (IOException exception) {
            throw new UploadedFileReadException(
                    "첨부파일을 읽지 못했습니다.",
                    exception
            );
        }
    }
}
