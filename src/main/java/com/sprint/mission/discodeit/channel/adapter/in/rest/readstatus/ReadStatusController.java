package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus;

import java.net.URI;
import com.sprint.mission.discodeit.channel.application.readstatus.ReadStatusControllerService;
import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response.ReadStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    // POST /api/readStatuses?userId=xxx&channelId=yyy - 읽음 상태 생성
    // Location은 단건 조회 경로가 없으므로 사용자별 목록을 가리킨다.
    @PostMapping
    public ResponseEntity<ReadStatusDto> create(
            @RequestParam UUID userId,
            @RequestParam UUID channelId
    ) {
        ReadStatusDto created = readStatusService.create(userId, channelId);
        return ResponseEntity
                .created(URI.create("/api/readStatuses?userId=" + userId))
                .body(created);
    }

    // GET /api/readStatuses?userId=xxx - 사용자의 모든 읽음 상태 조회
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    // PATCH /api/readStatuses?userId=xxx&channelId=yyy - 마지막 읽음 시각 갱신
    @PatchMapping
    public ResponseEntity<ReadStatusDto> updateLastReadAt(
            @RequestParam UUID userId,
            @RequestParam UUID channelId
    ) {
        return ResponseEntity.ok(readStatusService.updateLastReadAt(userId, channelId));
    }
}
