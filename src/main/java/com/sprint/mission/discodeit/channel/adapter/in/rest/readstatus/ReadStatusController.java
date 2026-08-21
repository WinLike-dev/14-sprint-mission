package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus;

import java.net.URI;
import jakarta.validation.Valid;
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
public class ReadStatusController {

    private final ReadStatusControllerService readStatusService; // 실제 비즈니스 로직을 처리하는 서비스

    // POST /api/readStatuses - 읽음 상태 생성
    // Location은 만들어진 읽음 상태를 가리킨다. 그 URI로 PATCH가 동작한다.
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

    // GET /api/readStatuses?userId=xxx - 사용자의 모든 읽음 상태 조회
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    // PATCH /api/readStatuses/{readStatusId} - 마지막 읽음 시각 갱신
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> updateLastReadAt(
            @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusDto updated =
                readStatusService.updateLastReadAt(readStatusId, request.newLastReadAt());
        return ResponseEntity.ok(updated);
    }
}
