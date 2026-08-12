package com.sprint.mission.discodeit.userstatus.service;

import com.sprint.mission.discodeit.userstatus.dto.response.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusControllerService {

    // 설계: 단건 동작은 UserStatus ID가 아니라 연결된 사용자 ID를 받는다.
    UserStatusDto find(UUID userId);

    List<UserStatusDto> findAll();

    UserStatusDto update(UUID userId);
}
