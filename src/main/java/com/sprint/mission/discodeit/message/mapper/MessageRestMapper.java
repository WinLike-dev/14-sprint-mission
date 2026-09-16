package com.sprint.mission.discodeit.message.mapper;

import com.sprint.mission.discodeit.common.exception.exceptions.UploadedFileReadException;
import com.sprint.mission.discodeit.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.dto.response.MessageDto;
import com.sprint.mission.discodeit.message.service.dto.CreateMessageCommand;
import com.sprint.mission.discodeit.message.service.dto.MessageAttachmentCommand;
import com.sprint.mission.discodeit.message.service.dto.MessageResult;
import com.sprint.mission.discodeit.message.service.dto.UpdateMessageCommand;
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
