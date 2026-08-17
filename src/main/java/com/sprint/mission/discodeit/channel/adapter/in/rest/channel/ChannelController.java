package com.sprint.mission.discodeit.channel.adapter.in.rest.channel;

import com.sprint.mission.discodeit.channel.application.channel.ChannelControllerService;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.response.ChannelDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 채널 REST inbound 어댑터.
 * HTTP 요청을 받아 ChannelControllerService에 위임하고, 결과만 HTTP 응답으로 바꾼다.
 * 기본 경로: /api/channels
 */
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelControllerService channelService; // 실제 비즈니스 로직을 처리하는 서비스

    // POST /api/channels/public - 공개 채널 생성
    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublic(@RequestBody PublicChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPublic(request));
    }

    // POST /api/channels/private - 비공개(DM) 채널 생성
    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivate(@RequestBody PrivateChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPrivate(request));
    }

    // GET /api/channels/{id} - 채널 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ChannelDto> find(@PathVariable UUID id) {
        return ResponseEntity.ok(channelService.find(id));
    }

    // GET /api/channels?userId=xxx - 사용자가 접근 가능한 모든 채널 목록 조회
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

    // PUT /api/channels/{id} - 채널 정보 수정 (PUBLIC 채널만 가능)
    @PutMapping("/{id}")
    public ResponseEntity<ChannelDto> update(
            @PathVariable UUID id,
            @RequestBody ChannelUpdateRequest request
    ) {
        return ResponseEntity.ok(channelService.update(id, request));
    }

    // DELETE /api/channels/{id} - 채널 삭제 (관련 ReadStatus도 함께 삭제됨)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
