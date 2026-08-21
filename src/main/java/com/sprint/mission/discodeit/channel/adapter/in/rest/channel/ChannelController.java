package com.sprint.mission.discodeit.channel.adapter.in.rest.channel;

import java.net.URI;
import jakarta.validation.Valid;
import com.sprint.mission.discodeit.channel.application.channel.ChannelControllerService;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.response.ChannelDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * 채널 REST inbound 어댑터.
 * HTTP 요청을 받아 ChannelControllerService에 위임하고, 결과만 HTTP 응답으로 바꾼다.
 * 기본 경로: /api/channels
 */
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

    private final ChannelControllerService channelService; // 실제 비즈니스 로직을 처리하는 서비스

    @Operation(summary = "Public Channel 생성")
    @ApiResponse(
            responseCode = "201",
            description = "Public Channel이 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = ChannelDto.class))
    )
    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublic(
            @Valid @RequestBody PublicChannelCreateRequest request
    ) {
        ChannelDto created = channelService.createPublic(request);
        return ResponseEntity.created(URI.create("/api/channels/" + created.id())).body(created);
    }

    @Operation(summary = "Private Channel 생성")
    @ApiResponse(
            responseCode = "201",
            description = "Private Channel이 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = ChannelDto.class))
    )
    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivate(
            @Valid @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelDto created = channelService.createPrivate(request);
        return ResponseEntity.created(URI.create("/api/channels/" + created.id())).body(created);
    }

    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @Parameter(description = "조회할 User ID") @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

    @Operation(summary = "Channel 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Private Channel은 수정할 수 없음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content
            )
    })
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update(
            @Parameter(description = "수정할 Channel ID") @PathVariable UUID channelId,
            @Valid @RequestBody PublicChannelUpdateRequest request
    ) {
        return ResponseEntity.ok(channelService.update(channelId, request));
    }

    // 관련 ReadStatus와 Message도 함께 삭제된다.
    @Operation(summary = "Channel 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Channel이 성공적으로 삭제됨",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content
            )
    })
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Channel ID") @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }
}
