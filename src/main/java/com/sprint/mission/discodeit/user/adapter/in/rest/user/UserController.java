package com.sprint.mission.discodeit.user.adapter.in.rest.user;

import com.sprint.mission.discodeit.user.application.user.dto.CreateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UpdateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UserProfileCommand;
import com.sprint.mission.discodeit.user.application.user.UserControllerService;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserProfileCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.response.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PostMapping
    public ResponseEntity<UserDto> create(
            @RequestBody UserCreateRequest request,
            @RequestParam(required = false) UserProfileCreateRequest profile
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserDto.from(userService.create(toCreateCommand(request, profile))));
    }

    // GET /api/users/{id} - 특정 사용자를 ID로 조회한다.
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> find(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(UserDto.from(userService.find(id)));
    }

    // GET /api/users - 모든 사용자 목록을 조회한다.
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream().map(UserDto::from).toList());
    }

    // PUT /api/users/{id} - 기존 사용자 정보를 수정한다.
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequest request,
            @RequestParam(required = false) UserProfileCreateRequest profile
    ) {
        return ResponseEntity.ok(UserDto.from(userService.update(id, toUpdateCommand(request, profile))));
    }

    // DELETE /api/users/{id} - 사용자를 삭제한다. 성공 시 204(No Content) 응답.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CreateUserCommand toCreateCommand(
            UserCreateRequest request,
            UserProfileCreateRequest profile
    ) {
        return new CreateUserCommand(
                request.username(),
                request.email(),
                request.password(),
                toProfileCommand(profile)
        );
    }

    private UpdateUserCommand toUpdateCommand(
            UserUpdateRequest request,
            UserProfileCreateRequest profile
    ) {
        return new UpdateUserCommand(
                request.username(),
                request.email(),
                request.password(),
                toProfileCommand(profile)
        );
    }

    private UserProfileCommand toProfileCommand(UserProfileCreateRequest profile) {
        if (profile == null) {
            return null;
        }
        return new UserProfileCommand(profile.fileName(), profile.contentType(), profile.bytes());
    }
}
