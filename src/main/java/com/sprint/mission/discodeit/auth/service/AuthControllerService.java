package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.auth.dto.request.LoginRequest;
import com.sprint.mission.discodeit.user.dto.response.UserDto;

// 설계: ControllerService 접미사는 컨트롤러가 호출하는 애플리케이션 경계임을 나타낸다.
public interface AuthControllerService {

    UserDto login(LoginRequest request);
}
