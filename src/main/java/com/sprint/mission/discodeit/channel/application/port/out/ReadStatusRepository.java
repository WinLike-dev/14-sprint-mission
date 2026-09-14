package com.sprint.mission.discodeit.channel.application.port.out;

import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 읽음 상태 저장소 outbound 포트.
 * 기본 CRUD 외에 사용자/채널 기준 조회·삭제를 메서드 이름 기반 쿼리로 제공한다.
 */
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    // 특정 사용자의 모든 읽음 상태를 조회 (사용자가 참여 중인 모든 채널의 읽음 상태)
    List<ReadStatus> findAllByUserId(UUID userId);

    // 특정 채널의 모든 읽음 상태를 조회 (해당 채널에 참여 중인 모든 사용자의 읽음 상태)
    List<ReadStatus> findAllByChannelId(UUID channelId);

    // 특정 사용자 + 특정 채널 조합의 읽음 상태를 조회 (1:1 매핑)
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    // 해당 조합의 읽음 상태가 이미 있는지만 확인한다.
    // 중복 등록 검사처럼 객체가 아니라 존재 여부만 필요한 경우를 위한 메서드다.
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    // 메서드 이름 기반 삭제 쿼리는 트랜잭션 안에서만 실행된다.
    // 서비스에 트랜잭션이 아직 없으므로 메서드에 직접 선언한다.

    // 특정 사용자의 모든 읽음 상태를 삭제 (사용자 탈퇴 시 사용)
    @Transactional
    void deleteAllByUserId(UUID userId);

    // 특정 채널의 모든 읽음 상태를 삭제 (채널 삭제 시 사용)
    @Transactional
    void deleteAllByChannelId(UUID channelId);
}
