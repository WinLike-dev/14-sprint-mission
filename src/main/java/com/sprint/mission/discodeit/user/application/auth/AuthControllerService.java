package com.sprint.mission.discodeit.user.application.auth;

import com.sprint.mission.discodeit.user.adapter.in.rest.auth.dto.request.LoginRequest;
import com.sprint.mission.discodeit.user.application.user.dto.UserResult;

/**
 * 인증(Authentication) 관련 비즈니스 로직의 애플리케이션 서비스 인터페이스.
 * 컨트롤러(AuthController)가 호출하는 경계 역할을 한다.
 */
// 설계: ControllerService 접미사는 컨트롤러가 호출하는 애플리케이션 경계임을 나타낸다.
public interface AuthControllerService {

    // 사용자 이름과 비밀번호로 로그인을 시도하고, 성공하면 사용자 정보를 반환한다.
    UserResult login(LoginRequest request);
}
