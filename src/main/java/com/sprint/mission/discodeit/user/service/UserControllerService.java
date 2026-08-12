package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.binarycontent.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.dto.response.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserControllerService {

    UserDto create(UserCreateRequest request, BinaryContentCreateRequest profile);

    UserDto find(UUID id);

    List<UserDto> findAll();

    UserDto update(UUID id, UserUpdateRequest request, BinaryContentCreateRequest profile);

    void delete(UUID id);
}
