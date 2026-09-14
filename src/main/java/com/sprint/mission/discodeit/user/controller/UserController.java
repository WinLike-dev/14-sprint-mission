package com.sprint.mission.discodeit.user.controller;

import com.sprint.mission.discodeit.user.mapper.UserRestMapper;
import com.sprint.mission.discodeit.user.mapper.UserStatusRestMapper;
import com.sprint.mission.discodeit.user.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.user.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.dto.response.UserDto;
import com.sprint.mission.discodeit.user.service.UserStatusControllerService;
import com.sprint.mission.discodeit.user.service.UserControllerService;
import com.sprint.mission.discodeit.user.service.dto.CreateUserCommand;
import com.sprint.mission.discodeit.user.service.dto.UpdateUserCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
public class UserController implements UserApi {

    private final UserControllerService userService;
    private final UserStatusControllerService userStatusService;
    private final UserRestMapper userMapper;
    private final UserStatusRestMapper userStatusMapper;

    // 프로필 이미지를 함께 받을 수 있으므로 multipart로 받는다.
    // 201과 함께 Location으로 만들어진 리소스의 위치를 알려준다.
    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> create(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        CreateUserCommand command = userMapper.toCommand(request, profile);
        UserDto created = userMapper.toResponse(userService.create(command));

        return ResponseEntity
                .created(URI.create("/api/users/" + created.id()))
                .body(created);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> responses = userMapper.toResponses(userService.findAll());
        return ResponseEntity.ok(responses);
    }

    @Override
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> update(
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UpdateUserCommand command = userMapper.toCommand(request, profile);
        UserDto response = userMapper.toResponse(
                userService.update(userId, command)
        );
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId
    ) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // 접속 상태는 사용자에 종속된 정보이므로 사용자 하위 경로로 노출한다.
    @Override
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                userStatusMapper.toResponse(
                        userStatusService.update(userId, request.newLastActiveAt())
                )
        );
    }
}
