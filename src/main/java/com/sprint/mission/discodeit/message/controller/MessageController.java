package com.sprint.mission.discodeit.message.controller;

import com.sprint.mission.discodeit.message.mapper.MessageRestMapper;
import com.sprint.mission.discodeit.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.dto.response.MessageDto;
import com.sprint.mission.discodeit.message.service.MessageControllerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
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
public class MessageController implements MessageApi {

    private final MessageControllerService messageService;
    private final MessageRestMapper messageMapper;

    // 첨부파일을 함께 받을 수 있으므로 multipart로 받는다.
    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        MessageDto created = messageMapper.toResponse(
                messageService.create(messageMapper.toCommand(request, attachments))
        );
        return ResponseEntity.created(URI.create("/api/messages/" + created.id())).body(created);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<MessageDto>> findAllByChannelId(
            @RequestParam UUID channelId
    ) {
        return ResponseEntity.ok(
                messageMapper.toResponses(messageService.findAllByChannelId(channelId))
        );
    }

    @Override
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> update(
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest request
    ) {
        return ResponseEntity.ok(
                messageMapper.toResponse(
                        messageService.update(messageId, messageMapper.toCommand(request))
                )
        );
    }

    @Override
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

}
