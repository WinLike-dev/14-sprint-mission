package com.sprint.mission.discodeit.userstatus.service;

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
    public UserStatusDto find(UUID userId) {
        return UserStatusDto.from(getStatusByUserId(userId));
    }

    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream().map(UserStatusDto::from).toList();
    }

    @Override
    public UserStatusDto update(UUID userId) {
        UserStatus status = getStatusByUserId(userId);
        status.updateLastActiveAt();
        return UserStatusDto.from(userStatusRepository.update(status));
    }

    private UserStatus getStatusByUserId(UUID userId) {
        Objects.requireNonNull(
                userId,
                "userId는 null일 수 없습니다."
        );
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserStatus.class, userId));
    }

    // 사실 왜 존재해야하는 지 모르겠다 요구사항 음..
    @Override
    public void createForUser(UUID userId) {
        userRepository.getById(userId);
        // Optional 에서 현재 존재하면 중복으로 처리
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateAssociationException(UserStatus.class, "userId=" + userId);
        }
        userStatusRepository.create(new UserStatus(userId, Instant.now()));
    }

    // 이것도 userId로 지우는게 맞는 것 같아 요구사항에서 벗어난 설계
    @Override
    public void deleteByUserId(UUID userId) {
        if (userStatusRepository.findByUserId(userId).isEmpty()) {
            throw new EntityNotFoundException(UserStatus.class, userId);
        }
        userStatusRepository.deleteByUserId(userId);
    }
}
