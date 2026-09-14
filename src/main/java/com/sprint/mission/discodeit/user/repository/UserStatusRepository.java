package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 상태 저장소 outbound 포트.
 * 사용자 ID 기준 조회를 메서드 이름 기반 쿼리로 정의한다.
 */
public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

    // 사용자 ID로 해당 사용자의 온라인 상태를 조회한다.
    Optional<UserStatus> findByUserId(UUID userId);
}
