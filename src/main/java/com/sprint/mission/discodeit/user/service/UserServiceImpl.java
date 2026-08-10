package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.binarycontent.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.user.dto.response.UserDto;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.common.exception.DuplicateFieldValueException;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import com.sprint.mission.discodeit.binarycontent.service.InternalBinaryContentService;
import com.sprint.mission.discodeit.userstatus.service.InternalUserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
// DI: final 협력 객체를 받는 생성자를 Lombok이 만들고 Spring이 Repository/내부 역할 Bean을 주입한다.
@RequiredArgsConstructor
public class UserServiceImpl implements UserControllerService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final InternalBinaryContentService internalBinaryContentService;
    private final InternalUserStatusService internalUserStatusService;

    @Override
    public UserDto create(UserCreateRequest request, BinaryContentCreateRequest profile) {
        UserCreateRequest target = Objects.requireNonNull(request);
        // 유일한 값인지 확인하기 유저 명과 이메일
        validateUniqueFields(null, target.username(), target.email());

        BinaryContentDto createdProfile = null;
        User user = null;

        try {
            createdProfile = profile == null
                    ? null
                    : internalBinaryContentService.create(profile);
            user = new User(
                    target.username(),
                    target.email(),
                    target.password(),
                    createdProfile == null ? null : createdProfile.id()
            );
            userRepository.create(user);
            internalUserStatusService.createForUser(user.getId());
            return createResponse(user);
        } catch (RuntimeException exception) {
            rollbackCreatedUser(user, createdProfile, exception);
            throw exception;
        }
    }

    @Override
    public UserDto find(UUID id) {
        return createResponse(userRepository.getById(id));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::createResponse)
                .toList();
    }

    @Override
    public UserDto update(
            UUID id,
            UserUpdateRequest request,
            BinaryContentCreateRequest profile
    ) {
        User user = userRepository.getById(id);
        UserUpdateRequest target = Objects.requireNonNull(request);
        validateUniqueFields(id, target.username(), target.email());

        UUID previousProfileId = user.getProfileId();
        BinaryContentDto newProfile = profile == null
                ? null
                : internalBinaryContentService.create(profile);
        UUID nextProfileId = newProfile == null ? previousProfileId : newProfile.id();

        try {
            user.update(target.username(), target.email(), target.password(), nextProfileId);
            userRepository.update(user);
        } catch (RuntimeException exception) {
            if (newProfile != null) {
                suppressCleanupFailure(
                        exception,
                        () -> internalBinaryContentService.delete(newProfile.id())
                );
            }
            throw exception;
        }
        // 새 프로필이 실제로 생기고 기존 프로필도 있었다면 삭제한 후 응답을 만든다.
        if (newProfile != null && previousProfileId != null) {
            internalBinaryContentService.delete(previousProfileId);
        }
        return createResponse(user);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.getById(id);
        internalUserStatusService.deleteByUserId(id);
        readStatusRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
        if (user.getProfileId() != null) {
            internalBinaryContentService.delete(user.getProfileId());
        }
    }

    private void validateUniqueFields(UUID currentId, String username, String email) {
        if (username != null) {
            userRepository.findByUsername(username)
                    .filter(user -> !user.getId().equals(currentId))
                    .ifPresent(user -> {
                        throw new DuplicateFieldValueException(User.class, "username", username);
                    });
        }
        if (email != null) {
            userRepository.findAll().stream()
                    .filter(user -> user.getEmail().equals(email))
                    .filter(user -> !user.getId().equals(currentId))
                    .findFirst()
                    .ifPresent(user -> {
                        throw new DuplicateFieldValueException(User.class, "email", email);
                    });
        }
    }

    private UserDto createResponse(User user) {
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(UserStatus.class, user.getId()));
        return UserDto.from(user, status.isOnline());
    }

    private void rollbackCreatedUser(
            User user,
            BinaryContentDto profile,
            RuntimeException original
    ) {
        if (user != null && userRepository.existsById(user.getId())) {
            suppressCleanupFailure(original, () -> userRepository.deleteById(user.getId()));
        }
        if (profile != null) {
            suppressCleanupFailure(
                    original,
                    () -> internalBinaryContentService.delete(profile.id())
            );
        }
    }

    private void suppressCleanupFailure(RuntimeException original, Runnable cleanup) {
        try {
            cleanup.run();
        } catch (RuntimeException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }
}
