package com.sprint.mission.discodeit.channel.application.port.out;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 읽음 상태 저장소 outbound 포트.
 * 기본 CRUD 외에 사용자/채널 기준 조회·삭제를 제공한다.
 */
public interface ReadStatusRepository extends CrudRepository<ReadStatus> {

    // 특정 사용자의 모든 읽음 상태를 조회 (사용자가 참여 중인 모든 채널의 읽음 상태)
    List<ReadStatus> findAllByUserId(UUID userId);

    // 특정 채널의 모든 읽음 상태를 조회 (해당 채널에 참여 중인 모든 사용자의 읽음 상태)
    List<ReadStatus> findAllByChannelId(UUID channelId);

    // 특정 사용자 + 특정 채널 조합의 읽음 상태를 조회 (1:1 매핑)
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    // 특정 사용자의 모든 읽음 상태를 삭제 (사용자 탈퇴 시 사용)
    void deleteAllByUserId(UUID userId);

    // 특정 채널의 모든 읽음 상태를 삭제 (채널 삭제 시 사용)
    void deleteAllByChannelId(UUID channelId);
}
