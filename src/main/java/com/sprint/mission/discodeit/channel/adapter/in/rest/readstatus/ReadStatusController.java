package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus;

import com.sprint.mission.discodeit.channel.application.readstatus.ReadStatusControllerService;
import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response.ReadStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 읽음 상태 REST inbound 어댑터.
 * HTTP 요청을 받아 ReadStatusControllerService에 위임한다.
 * 기본 경로: /api/read-statuses
 */
@RestController
@RequestMapping("/api/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusControllerService readStatusService; // 실제 비즈니스 로직을 처리하는 서비스

    // POST /api/read-statuses?userId=xxx&channelId=yyy - 읽음 상태 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto> create(
            @RequestParam UUID userId,
            @RequestParam UUID channelId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(userId, channelId));
    }

    // GET /api/read-statuses/find?userId=xxx&channelId=yyy - 특정 읽음 상태 조회
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<ReadStatusDto> find(
            @RequestParam UUID userId,
            @RequestParam UUID channelId
    ) {
        return ResponseEntity.ok(readStatusService.find(userId, channelId));
    }

    // GET /api/read-statuses?userId=xxx - 사용자의 모든 읽음 상태 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }

    // PUT /api/read-statuses?userId=xxx&channelId=yyy - 마지막 읽음 시각 갱신
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ReadStatusDto> updateLastReadAt(
            @RequestParam UUID userId,
            @RequestParam UUID channelId
    ) {
        return ResponseEntity.ok(readStatusService.updateLastReadAt(userId, channelId));
    }

    // DELETE /api/read-statuses?userId=xxx&channelId=yyy - 읽음 상태 삭제
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
            @RequestParam UUID userId,
            @RequestParam UUID channelId
    ) {
        readStatusService.delete(userId, channelId);
        return ResponseEntity.noContent().build();
    }
}
