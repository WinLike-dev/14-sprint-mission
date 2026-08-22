package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.sprint.mission.discodeit.channel.application.readstatus.ReadStatusControllerService;
import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.request.ReadStatusUpsertRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response.ReadStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
 *
 * 단건은 /{userId}/{channelId}로 지정한다. 한 사용자는 한 채널에 대해 읽음 상태를
 * 하나만 가지므로 그 조합이 곧 이 리소스의 주소다. 저장용 id는 응답에만 싣는다.
 * id로 주소를 지정하면 클라이언트가 고치기 전에 먼저 id를 알아내야 하고,
 * 그러려면 목록을 받아 캐시해 두어야 한다.
 */
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController {

    private final ReadStatusControllerService readStatusService; // 실제 비즈니스 로직을 처리하는 서비스

    // 없으면 만들고 있으면 갱신한다. 같은 요청을 몇 번 보내도 결과가 같다.
    //
    // 새로 만들어졌을 때 201을 돌려주지 않는 이유는, 그 구분이 곧 "이미 있었는지"를
    // 클라이언트에게 다시 알리는 일이기 때문이다. 이 API가 없애려는 것이 바로 그 구분이다.
    // 대상 주소도 요청 경로 그대로여서 새로 알릴 URI가 없다.
    @Operation(
            summary = "Message 읽음 상태 등록",
            description = "경로의 userId와 channelId가 대상을 지정한다. 없으면 생성하고 있으면 읽은 시각을 갱신한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "읽음 상태가 요청한 값으로 반영됨",
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
                    description = "비공개 채널에 읽음 상태를 새로 만들려 함",
                    content = @Content
            )
    })
    @PutMapping("/{userId}/{channelId}")
    public ResponseEntity<ReadStatusDto> upsert(
            @Parameter(description = "대상 User ID") @PathVariable UUID userId,
            @Parameter(description = "대상 Channel ID") @PathVariable UUID channelId,
            @Valid @RequestBody ReadStatusUpsertRequest request
    ) {
        return ResponseEntity.ok(
                readStatusService.upsert(userId, channelId, request.lastReadAt())
        );
    }

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @Parameter(description = "조회할 User ID") @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    @Operation(summary = "특정 Channel에 대한 Message 읽음 상태 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태 조회 성공",
                    content = @Content(schema = @Schema(implementation = ReadStatusDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 조합의 Message 읽음 상태를 찾을 수 없음",
                    content = @Content
            )
    })
    @GetMapping("/{userId}/{channelId}")
    public ResponseEntity<ReadStatusDto> find(
            @Parameter(description = "조회할 User ID") @PathVariable UUID userId,
            @Parameter(description = "조회할 Channel ID") @PathVariable UUID channelId
    ) {
        return ResponseEntity.ok(readStatusService.find(userId, channelId));
    }

    @Operation(summary = "Message 읽음 상태 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Message 읽음 상태가 성공적으로 삭제됨"),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 조합의 Message 읽음 상태를 찾을 수 없음",
                    content = @Content
            )
    })
    @DeleteMapping("/{userId}/{channelId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 읽음 상태의 User ID") @PathVariable UUID userId,
            @Parameter(description = "삭제할 읽음 상태의 Channel ID") @PathVariable UUID channelId
    ) {
        readStatusService.delete(userId, channelId);
        return ResponseEntity.noContent().build();
    }
}
