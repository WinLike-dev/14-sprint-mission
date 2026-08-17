package com.sprint.mission.discodeit.user.adapter.in.rest.status;

import com.sprint.mission.discodeit.user.application.status.UserStatusControllerService;
import com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.response.UserStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 사용자 상태 REST inbound 어댑터.
 * "/api/user-statuses" 요청을 UserStatusControllerService에 위임한다.
 */
@RestController
@RequestMapping("/api/user-statuses")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusControllerService userStatusService;

    // GET /api/user-statuses/{userId} - 특정 사용자의 온라인 상태를 조회한다.
    @GetMapping("/{userId}")
    public ResponseEntity<UserStatusDto> find(@PathVariable UUID userId) {
        return ResponseEntity.ok(userStatusService.find(userId));
    }

    // GET /api/user-statuses - 모든 사용자의 온라인 상태 목록을 조회한다.
    @GetMapping
    public ResponseEntity<List<UserStatusDto>> findAll() {
        return ResponseEntity.ok(userStatusService.findAll());
    }

    // PUT /api/user-statuses/{userId} - 특정 사용자의 마지막 활동 시각을 갱신한다.
    @PutMapping("/{userId}")
    public ResponseEntity<UserStatusDto> update(@PathVariable UUID userId) {
        return ResponseEntity.ok(userStatusService.update(userId));
    }
}
