package com.sprint.mission.discodeit.user.application.user;

import com.sprint.mission.discodeit.user.application.user.dto.CreateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UpdateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UserResult;

import java.util.List;
import java.util.UUID;

/**
 * 사용자 유스케이스 계약.
 * REST DTO가 아니라 애플리케이션 Command/Result만 받는다.
 */
public interface UserControllerService {

    UserResult create(CreateUserCommand command);

    UserResult find(UUID id);

    List<UserResult> findAll();

    UserResult update(UUID id, UpdateUserCommand command);

    void delete(UUID id);
}
