package com.sprint.mission.discodeit.user.adapter.in.rest.user;

import com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.response.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User API")
public interface UserApi {

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
    ResponseEntity<UserDto> create(UserCreateRequest request, MultipartFile profile);

    @Operation(summary = "전체 User 목록 조회")
    @ApiResponse(responseCode = "200", description = "User 목록 조회 성공")
    ResponseEntity<List<UserDto>> findAll();

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
    ResponseEntity<UserDto> update(
            @Parameter(description = "수정할 User ID") UUID userId,
            UserUpdateRequest request,
            MultipartFile profile
    );

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
    ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 User ID") UUID userId
    );

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
    ResponseEntity<UserStatusDto> updateUserStatusByUserId(
            @Parameter(description = "상태를 변경할 User ID") UUID userId,
            UserStatusUpdateRequest request
    );
}
