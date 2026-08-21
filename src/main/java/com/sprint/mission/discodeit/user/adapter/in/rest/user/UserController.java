package com.sprint.mission.discodeit.user.adapter.in.rest.user;

import com.sprint.mission.discodeit.user.application.user.dto.CreateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UpdateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UserProfileCommand;
import com.sprint.mission.discodeit.user.application.status.UserStatusControllerService;
import com.sprint.mission.discodeit.user.application.user.UserControllerService;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserProfileCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.response.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
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
    private final UserStatusControllerService userStatusService;

    // POST /api/users - 새 사용자를 생성한다.
    // 201과 함께 Location으로 만들어진 리소스의 위치를 알려준다.
    @PostMapping
    public ResponseEntity<UserDto> create(
            @Valid @RequestBody UserCreateRequest request
    ) {
        UserDto created = UserDto.from(userService.create(toCreateCommand(request)));
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
    }

    // GET /api/users - 모든 사용자 목록을 조회한다.
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream().map(UserDto::from).toList());
    }

    // PATCH /api/users/{userId} - 기존 사용자 정보를 수정한다.
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(UserDto.from(userService.update(userId, toUpdateCommand(request))));
    }

    // DELETE /api/users/{userId} - 사용자를 삭제한다. 성공 시 204(No Content) 응답.
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId
    ) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/users/{userId}/userStatus - 사용자의 마지막 활동 시각을 갱신한다.
    // 접속 상태는 사용자에 종속된 정보이므로 사용자 하위 경로로 노출한다.
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(userStatusService.update(userId));
    }

    private CreateUserCommand toCreateCommand(UserCreateRequest request) {
        return new CreateUserCommand(
                request.username(),
                request.email(),
                request.password(),
                toProfileCommand(request.profile())
        );
    }

    private UpdateUserCommand toUpdateCommand(UserUpdateRequest request) {
        return new UpdateUserCommand(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                toProfileCommand(request.profile())
        );
    }

    private UserProfileCommand toProfileCommand(UserProfileCreateRequest profile) {
        if (profile == null) {
            return null;
        }
        return new UserProfileCommand(profile.fileName(), profile.contentType(), profile.bytes());
    }
}
