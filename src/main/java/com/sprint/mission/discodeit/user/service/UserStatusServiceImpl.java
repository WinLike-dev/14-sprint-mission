package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.service.dto.UserStatusResult;
import com.sprint.mission.discodeit.user.entity.UserStatus;
import com.sprint.mission.discodeit.common.exception.exceptions.EntityNotFoundException;
import com.sprint.mission.discodeit.user.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
// 기본은 읽기 전용 트랜잭션이다. 쓰기 메서드만 @Transactional로 덮어쓴다.
@Transactional(readOnly = true)
public class UserStatusServiceImpl implements UserStatusControllerService {

    private final UserStatusRepository userStatusRepository;

    // 특정 사용자의 온라인 상태를 조회하여 결과 모델로 반환한다.
    @Override
    public UserStatusResult find(UUID userId) {
        return UserStatusResult.from(getStatusByUserId(userId));
    }

    // 모든 사용자의 온라인 상태를 조회하여 결과 모델 목록으로 반환한다.
    @Override
    public List<UserStatusResult> findAll() {
        return userStatusRepository.findAll().stream().map(UserStatusResult::from).toList();
    }

    // 사용자의 마지막 활동 시각을 갱신하고, 갱신된 상태를 반환한다.
    // 영속 상태라 변경 감지로 반영되므로 save를 부르지 않는다.
    @Override
    @Transactional
    public UserStatusResult update(UUID userId, Instant newLastActiveAt) {
        UserStatus status = getStatusByUserId(userId);
        status.updateLastActiveAt(newLastActiveAt);
        return UserStatusResult.from(status);
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
