package com.sprint.mission.discodeit.user.application.port.out;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.user.domain.status.UserStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 상태 저장소 outbound 포트.
 * 사용자 ID 기준 조회/삭제를 추가로 정의한다.
 *
 * 파생 조회는 저장 기술과 무관한 도메인 기준 필터링이므로 default로 한 번만 정의한다.
 * 구현체가 인덱스 등으로 더 잘할 수 있다면 개별적으로 재정의하면 된다.
 */
public interface UserStatusRepository extends CrudRepository<UserStatus> {

    // 사용자 ID로 해당 사용자의 온라인 상태를 조회한다.
    default Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    // 사용자 ID에 해당하는 온라인 상태를 삭제한다. 사용자 탈퇴 시 함께 정리할 때 사용.
    default void deleteByUserId(UUID userId) {
        findByUserId(userId).ifPresent(status -> deleteById(status.getId()));
    }
}
