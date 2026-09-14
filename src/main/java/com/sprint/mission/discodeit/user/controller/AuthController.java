package com.sprint.mission.discodeit.user.controller;

import com.sprint.mission.discodeit.user.mapper.AuthRestMapper;
import jakarta.validation.Valid;
import com.sprint.mission.discodeit.user.service.AuthControllerService;
import com.sprint.mission.discodeit.user.dto.request.LoginRequest;
import com.sprint.mission.discodeit.user.dto.response.UserDto;
import com.sprint.mission.discodeit.user.mapper.UserRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 REST 컨트롤러.
 * "/api/auth" 경로의 로그인 요청을 AuthControllerService에 위임한다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthControllerService authService;
    private final AuthRestMapper authMapper;
    private final UserRestMapper userMapper;

    // 존재하지 않는 username과 틀린 password를 구분해 알리지 않는다.
    // 둘을 나누면 어떤 username이 등록되어 있는지 알려주는 셈이 된다.
    @Override
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                userMapper.toResponse(
                        authService.login(authMapper.toCommand(request))
                )
        );
    }
}
