package com.sprint.mission.discodeit.user.controller.swagger;

import com.sprint.mission.discodeit.user.dto.request.LoginRequest;
import com.sprint.mission.discodeit.user.dto.response.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "username 또는 password가 일치하지 않음",
                    content = @Content
            )
    })
    ResponseEntity<UserDto> login(LoginRequest request);
}
