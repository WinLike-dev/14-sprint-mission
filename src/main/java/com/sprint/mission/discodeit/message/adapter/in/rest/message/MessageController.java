package com.sprint.mission.discodeit.message.adapter.in.rest.message;

import java.net.URI;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Message", description = "Message API")
public class MessageController {

    private final MessageControllerService messageService;

    // 첨부파일을 함께 받을 수 있으므로 multipart로 받는다.
    @Operation(summary = "Message 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Message가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = MessageDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        MessageDto created = messageService.create(toCreateCommand(request, attachments));
        return ResponseEntity.created(URI.create("/api/messages/" + created.id())).body(created);
    }

    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<MessageDto>> findAllByChannelId(
            @Parameter(description = "조회할 Channel ID") @RequestParam UUID channelId
    ) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    @Operation(summary = "Message 내용 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content
            )
    })
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> update(
            @Parameter(description = "수정할 Message ID") @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest request
    ) {
        return ResponseEntity.ok(messageService.update(messageId, request));
    }

    @Operation(summary = "Message 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Message가 성공적으로 삭제됨",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content
            )
    })
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Message ID") @PathVariable UUID messageId
    ) {
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
