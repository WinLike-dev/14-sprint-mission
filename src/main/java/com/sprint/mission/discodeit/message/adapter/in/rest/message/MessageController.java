package com.sprint.mission.discodeit.message.adapter.in.rest.message;

import com.sprint.mission.discodeit.message.application.message.MessageControllerService;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.response.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MessageDto> create(@RequestBody MessageCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(request));
    }

    // GET /api/messages?channelId=... - 특정 채널의 모든 메시지를 조회한다
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessageDto>> findAllByChannelId(@RequestParam UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    // PUT /api/messages/{id} - 메시지 내용을 수정한다
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<MessageDto> update(
            @PathVariable UUID id,
            @RequestBody MessageUpdateRequest request
    ) {
        return ResponseEntity.ok(messageService.update(id, request));
    }

    // DELETE /api/messages/{id} - 메시지를 삭제하고 204 No Content를 반환한다
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
