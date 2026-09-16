package com.sprint.mission.discodeit.channel.controller;

import java.net.URI;

import com.sprint.mission.discodeit.channel.controller.swagger.ChannelApi;
import jakarta.validation.Valid;
import com.sprint.mission.discodeit.channel.service.ChannelControllerService;
import com.sprint.mission.discodeit.channel.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.response.ChannelDto;
import com.sprint.mission.discodeit.channel.mapper.ChannelRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 채널 REST 컨트롤러.
 * HTTP 요청을 받아 ChannelControllerService에 위임하고, 결과만 HTTP 응답으로 바꾼다.
 * 기본 경로: /api/channels
 */
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

    private final ChannelControllerService channelService; // 실제 비즈니스 로직을 처리하는 서비스
    private final ChannelRestMapper channelMapper;

    @Override
    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublic(
            @Valid @RequestBody PublicChannelCreateRequest request
    ) {
        ChannelDto created = channelMapper.toResponse(
                channelService.createPublic(channelMapper.toCommand(request))
        );
        return ResponseEntity.created(URI.create("/api/channels/" + created.id())).body(created);
    }

    @Override
    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivate(
            @Valid @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelDto created = channelMapper.toResponse(
                channelService.createPrivate(channelMapper.toCommand(request))
        );
        return ResponseEntity.created(URI.create("/api/channels/" + created.id())).body(created);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(
                channelMapper.toResponses(channelService.findAllByUserId(userId))
        );
    }

    @Override
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update(
            @PathVariable UUID channelId,
            @Valid @RequestBody PublicChannelUpdateRequest request
    ) {
        return ResponseEntity.ok(
                channelMapper.toResponse(
                        channelService.update(channelId, channelMapper.toCommand(request))
                )
        );
    }

    // 관련 ReadStatus와 Message도 함께 삭제된다.
    @Override
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }
}
