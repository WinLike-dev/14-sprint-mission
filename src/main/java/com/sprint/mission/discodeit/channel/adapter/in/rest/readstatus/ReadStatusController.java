package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus;

import java.net.URI;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.sprint.mission.discodeit.channel.application.readstatus.ReadStatusControllerService;
import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response.ReadStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 읽음 상태 REST inbound 어댑터.
 * HTTP 요청을 받아 ReadStatusControllerService에 위임한다.
 * 기본 경로: /api/readStatuses
 */
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

    private final ReadStatusControllerService readStatusService; // 실제 비즈니스 로직을 처리하는 서비스

    // Location은 만들어진 읽음 상태를 가리킨다. 그 URI로 PATCH가 동작한다.
    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Message 읽음 상태가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = ReadStatusDto.class))
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 읽음 상태가 존재함",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<ReadStatusDto> create(
            @Valid @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatusDto created = readStatusService.create(
                request.userId(), request.channelId(), request.lastReadAt()
        );
        return ResponseEntity
                .created(URI.create("/api/readStatuses/" + created.id()))
                .body(created);
    }

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @Parameter(description = "조회할 User ID") @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태가 성공적으로 수정됨"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content
            )
    })
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> updateLastReadAt(
            @Parameter(description = "수정할 읽음 상태 ID") @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusDto updated =
                readStatusService.updateLastReadAt(readStatusId, request.newLastReadAt());
        return ResponseEntity.ok(updated);
    }
}
