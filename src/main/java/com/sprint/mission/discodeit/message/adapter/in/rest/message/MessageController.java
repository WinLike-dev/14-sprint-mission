package com.sprint.mission.discodeit.message.adapter.in.rest.message;

import java.net.URI;
import jakarta.validation.Valid;
import com.sprint.mission.discodeit.message.application.message.MessageControllerService;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.response.MessageDto;
import com.sprint.mission.discodeit.message.application.message.dto.CreateMessageCommand;
import com.sprint.mission.discodeit.message.application.message.dto.MessageAttachmentCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

/**
 * 메시지 REST inbound 어댑터.
 * HTTP 요청을 받아 MessageControllerService에 위임한다.
 * 엔드포인트: /api/messages
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageControllerService messageService;

    // POST /api/messages - 새 메시지를 생성하고 201 Created를 반환한다
    // 첨부파일을 함께 받을 수 있으므로 multipart로 받는다.
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        MessageDto created = messageService.create(toCreateCommand(request, attachments));
        return ResponseEntity.created(URI.create("/api/messages/" + created.id())).body(created);
    }

    // GET /api/messages?channelId=... - 특정 채널의 모든 메시지를 조회한다
    @GetMapping
    public ResponseEntity<List<MessageDto>> findAllByChannelId(@RequestParam UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    // PATCH /api/messages/{messageId} - 메시지 내용을 수정한다
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> update(
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest request
    ) {
        return ResponseEntity.ok(messageService.update(messageId, request));
    }

    // DELETE /api/messages/{messageId} - 메시지를 삭제하고 204 No Content를 반환한다
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    // JSON 파트와 파일 파트를 합쳐 유스케이스 입력을 만든다.
    private CreateMessageCommand toCreateCommand(
            MessageCreateRequest request,
            List<MultipartFile> attachments
    ) {
        return new CreateMessageCommand(
                request.content(),
                request.channelId(),
                request.authorId(),
                toAttachmentCommands(attachments)
        );
    }

    // 첨부 파트는 선택 항목이다. 프론트엔드는 첨부가 없으면 파트를 아예 보내지 않는다.
    private List<MessageAttachmentCommand> toAttachmentCommands(List<MultipartFile> attachments) {
        if (attachments == null) {
            return List.of();
        }
        return attachments.stream()
                .filter(attachment -> !attachment.isEmpty())
                .map(MessageController::toAttachmentCommand)
                .toList();
    }

    private static MessageAttachmentCommand toAttachmentCommand(MultipartFile attachment) {
        try {
            return new MessageAttachmentCommand(
                    attachment.getOriginalFilename(),
                    attachment.getContentType(),
                    attachment.getBytes()
            );
        } catch (IOException exception) {
            throw new UncheckedIOException("첨부파일을 읽지 못했습니다.", exception);
        }
    }
}
