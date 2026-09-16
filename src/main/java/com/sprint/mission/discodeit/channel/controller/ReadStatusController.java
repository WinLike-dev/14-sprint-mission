package com.sprint.mission.discodeit.channel.controller;

import java.net.URI;

import com.sprint.mission.discodeit.channel.controller.swagger.ReadStatusApi;
import jakarta.validation.Valid;
import com.sprint.mission.discodeit.channel.service.ReadStatusControllerService;
import com.sprint.mission.discodeit.channel.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.channel.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.channel.mapper.ReadStatusRestMapper;
import lombok.RequiredArgsConstructor;
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
 * 읽음 상태 REST 컨트롤러.
 * HTTP 요청을 받아 ReadStatusControllerService에 위임한다.
 * 기본 경로: /api/readStatuses
 */
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController implements ReadStatusApi {

    private final ReadStatusControllerService readStatusService; // 실제 비즈니스 로직을 처리하는 서비스
    private final ReadStatusRestMapper readStatusMapper;

    // Location은 만들어진 읽음 상태를 가리킨다. 그 URI로 PATCH가 동작한다.
    @Override
    @PostMapping
    public ResponseEntity<ReadStatusDto> create(
            @Valid @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatusDto created = readStatusMapper.toResponse(
                readStatusService.create(readStatusMapper.toCommand(request))
        );
        return ResponseEntity
                .created(URI.create("/api/readStatuses/" + created.id()))
                .body(created);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(
                readStatusMapper.toResponses(readStatusService.findAllByUserId(userId))
        );
    }

    @Override
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> updateLastReadAt(
            @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusDto updated = readStatusMapper.toResponse(
                readStatusService.updateLastReadAt(
                        readStatusId,
                        readStatusMapper.toCommand(request)
                )
        );
        return ResponseEntity.ok(updated);
    }
}
