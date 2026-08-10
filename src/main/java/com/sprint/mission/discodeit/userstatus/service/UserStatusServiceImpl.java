package com.sprint.mission.discodeit.userstatus.service;

import com.sprint.mission.discodeit.userstatus.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.userstatus.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.common.exception.DuplicateAssociationException;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl
        implements UserStatusControllerService, InternalUserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusDto find(UUID id) {
        return UserStatusDto.from(userStatusRepository.getById(id));
    }

    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream().map(UserStatusDto::from).toList();
    }

    @Override
    public UserStatusDto update(UUID id, UserStatusUpdateRequest request) {
        UserStatus status = userStatusRepository.getById(id);
        status.update(Objects.requireNonNull(request).lastActiveAt());
        return UserStatusDto.from(userStatusRepository.update(status));
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserStatus.class, userId));
        status.update(Objects.requireNonNull(request).lastActiveAt());
        return UserStatusDto.from(userStatusRepository.update(status));
    }

    @Override
    public void createForUser(UUID userId) {
        userRepository.getById(userId);
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateAssociationException(UserStatus.class, "userId=" + userId);
        }
        userStatusRepository.create(new UserStatus(userId, Instant.now()));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        if (userStatusRepository.findByUserId(userId).isEmpty()) {
            throw new EntityNotFoundException(UserStatus.class, userId);
        }
        userStatusRepository.deleteByUserId(userId);
    }
}
