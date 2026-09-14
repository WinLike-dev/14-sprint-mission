package com.sprint.mission.discodeit.user.application.internal;

import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.user.api.UserInternalApi;
import com.sprint.mission.discodeit.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * UserInternalApi의 구현체.
 * 다른 모듈은 UserRepository가 아니라 이 서비스를 통해 사용자 존재 여부만 확인한다.
 */
@Service
@RequiredArgsConstructor
public class UserInternalService implements UserInternalApi {

    private final UserRepository userRepository;

    // 해당 userId의 사용자가 존재하는지 확인한다. 존재하지 않으면 예외를 던진다.
    @Override
    public void requireExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class, userId);
        }
    }
}
