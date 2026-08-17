package com.sprint.mission.discodeit.user.adapter.in.rest.user;

import com.sprint.mission.discodeit.user.application.user.UserControllerService;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserProfileCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.response.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 사용자 REST inbound 어댑터.
 * HTTP 요청을 받아 UserControllerService에 위임한다.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserControllerService userService;

    // POST /api/users - 새 사용자를 생성한다. 성공 시 201(Created) 응답.
    @RequestMapping(method = org.springframework.web.bind.annotation.RequestMethod.POST)
    public ResponseEntity<UserDto> create(
            @org.springframework.web.bind.annotation.RequestBody UserCreateRequest request,
            @org.springframework.web.bind.annotation.RequestParam(required = false) UserProfileCreateRequest profile
    ) {
        UserDto created = userService.create(request, profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/users/{id} - 특정 사용자를 ID로 조회한다.
    @RequestMapping(value = "/{id}", method = org.springframework.web.bind.annotation.RequestMethod.GET)
    public ResponseEntity<UserDto> find(
            @org.springframework.web.bind.annotation.PathVariable UUID id
    ) {
        return ResponseEntity.ok(userService.find(id));
    }

    // GET /api/users - 모든 사용자 목록을 조회한다.
    @RequestMapping(method = org.springframework.web.bind.annotation.RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // PUT /api/users/{id} - 기존 사용자 정보를 수정한다.
    @RequestMapping(value = "/{id}", method = org.springframework.web.bind.annotation.RequestMethod.PUT)
    public ResponseEntity<UserDto> update(
            @org.springframework.web.bind.annotation.PathVariable UUID id,
            @org.springframework.web.bind.annotation.RequestBody UserUpdateRequest request,
            @org.springframework.web.bind.annotation.RequestParam(required = false) UserProfileCreateRequest profile
    ) {
        return ResponseEntity.ok(userService.update(id, request, profile));
    }

    // DELETE /api/users/{id} - 사용자를 삭제한다. 성공 시 204(No Content) 응답.
    @RequestMapping(value = "/{id}", method = org.springframework.web.bind.annotation.RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
            @org.springframework.web.bind.annotation.PathVariable UUID id
    ) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
