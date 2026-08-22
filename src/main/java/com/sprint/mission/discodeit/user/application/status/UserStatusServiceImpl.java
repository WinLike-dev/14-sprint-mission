package com.sprint.mission.discodeit.user.application.status;

import com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.user.domain.status.UserStatus;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * UserStatusControllerService의 구현체.
 * 사용자 온라인 상태를 조회하고, 마지막 활동 시각을 갱신하는 로직을 담당한다.
 */
@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusControllerService {

    private final UserStatusRepository userStatusRepository;

    // 특정 사용자의 온라인 상태를 조회하여 DTO로 반환한다.
    @Override
    public UserStatusDto find(UUID userId) {
        return UserStatusDto.from(getStatusByUserId(userId));
    }

    // 모든 사용자의 온라인 상태를 조회하여 DTO 리스트로 반환한다.
    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream().map(UserStatusDto::from).toList();
    }

    // 사용자의 마지막 활동 시각을 현재 시각으로 갱신하고, 갱신된 상태를 반환한다.
    @Override
    public UserStatusDto update(UUID userId, Instant newLastActiveAt) {
        UserStatus status = getStatusByUserId(userId);
        status.updateLastActiveAt(newLastActiveAt);
        return UserStatusDto.from(userStatusRepository.update(status));
    }

    // userId로 UserStatus를 찾는 내부 헬퍼 메서드. 없으면 예외를 던진다.
    private UserStatus getStatusByUserId(UUID userId) {
        Objects.requireNonNull(
                userId,
                "userId는 null일 수 없습니다."
        );
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserStatus.class, userId));
    }

}
