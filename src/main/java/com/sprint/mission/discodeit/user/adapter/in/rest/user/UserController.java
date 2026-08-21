package com.sprint.mission.discodeit.user.adapter.in.rest.user;

import com.sprint.mission.discodeit.user.application.user.dto.CreateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UpdateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UserProfileCommand;
import com.sprint.mission.discodeit.user.application.status.UserStatusControllerService;
import com.sprint.mission.discodeit.user.application.user.UserControllerService;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.response.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
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
    // 프로필 이미지를 함께 받을 수 있으므로 multipart로 받는다.
    // 201과 함께 Location으로 만들어진 리소스의 위치를 알려준다.
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> create(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UserDto created = UserDto.from(userService.create(toCreateCommand(request, profile)));
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
    }

    // GET /api/users - 모든 사용자 목록을 조회한다.
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream().map(UserDto::from).toList());
    }

    // PATCH /api/users/{userId} - 기존 사용자 정보를 수정한다.
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> update(
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UpdateUserCommand command = toUpdateCommand(request, profile);
        return ResponseEntity.ok(UserDto.from(userService.update(userId, command)));
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
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(userStatusService.update(userId, request.newLastActiveAt()));
    }

    private CreateUserCommand toCreateCommand(UserCreateRequest request, MultipartFile profile) {
        return new CreateUserCommand(
                request.username(),
                request.email(),
                request.password(),
                toProfileCommand(profile)
        );
    }

    private UpdateUserCommand toUpdateCommand(UserUpdateRequest request, MultipartFile profile) {
        return new UpdateUserCommand(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                toProfileCommand(profile)
        );
    }

    // 프로필 파트는 선택 항목이다. 없으면 null을 넘겨 "프로필 없음"(등록) 또는
    // "기존 프로필 유지"(수정)를 뜻하게 한다. 파트 이름만 오고 내용이 비어 있어도 같게 본다.
    private UserProfileCommand toProfileCommand(MultipartFile profile) {
        if (profile == null || profile.isEmpty()) {
            return null;
        }
        try {
            return new UserProfileCommand(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            );
        } catch (IOException exception) {
            throw new UncheckedIOException("프로필 이미지를 읽지 못했습니다.", exception);
        }
    }
}
