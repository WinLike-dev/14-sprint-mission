package com.sprint.mission.discodeit.userstatus.service;

import com.sprint.mission.discodeit.userstatus.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.userstatus.dto.response.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusControllerService {

    UserStatusDto find(UUID id);

    List<UserStatusDto> findAll();

    UserStatusDto update(UUID id, UserStatusUpdateRequest request);

    UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);
}
