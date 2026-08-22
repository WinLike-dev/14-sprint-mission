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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserControllerService userService;
    private final UserStatusControllerService userStatusService;

    // 프로필 이미지를 함께 받을 수 있으므로 multipart로 받는다.
    // 201과 함께 Location으로 만들어진 리소스의 위치를 알려준다.
    @Operation(summary = "User 등록")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "같은 username 또는 email을 사용하는 User가 이미 존재함",
                    content = @Content
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> create(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UserDto created = UserDto.from(userService.create(toCreateCommand(request, profile)));
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
    }

    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream().map(UserDto::from).toList());
    }

    @Operation(summary = "User 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨"),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "같은 username 또는 email을 사용하는 User가 이미 존재함",
                    content = @Content
            )
    })
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> update(
            @Parameter(description = "수정할 User ID") @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        UpdateUserCommand command = toUpdateCommand(request, profile);
        return ResponseEntity.ok(UserDto.from(userService.update(userId, command)));
    }

    @Operation(summary = "User 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User가 성공적으로 삭제됨",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content
            )
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 User ID") @PathVariable UUID userId
    ) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // 접속 상태는 사용자에 종속된 정보이므로 사용자 하위 경로로 노출한다.
    @Operation(summary = "User 온라인 상태 업데이트")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User 온라인 상태가 성공적으로 업데이트됨"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content
            )
    })
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
            @Parameter(description = "상태를 변경할 User ID") @PathVariable UUID userId,
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
