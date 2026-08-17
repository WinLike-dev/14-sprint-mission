package com.sprint.mission.discodeit.user.adapter.in.rest.auth;

import com.sprint.mission.discodeit.user.application.auth.AuthControllerService;
import com.sprint.mission.discodeit.user.adapter.in.rest.auth.dto.request.LoginRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.response.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 REST inbound 어댑터.
 * "/api/auth" 경로의 로그인 요청을 AuthControllerService에 위임한다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthControllerService authService;

    // POST /api/auth/login - 로그인 요청을 받아 인증 서비스에 위임한다.
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(UserDto.from(authService.login(request)));
    }
}
