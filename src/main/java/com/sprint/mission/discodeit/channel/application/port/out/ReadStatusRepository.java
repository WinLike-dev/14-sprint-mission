package com.sprint.mission.discodeit.channel.application.port.out;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 읽음 상태 저장소 outbound 포트.
 * 기본 CRUD 외에 사용자/채널 기준 조회·삭제를 제공한다.
 *
 * 파생 조회는 저장 기술과 무관한 도메인 기준 필터링이므로 default로 한 번만 정의한다.
 * 구현체가 인덱스 등으로 더 잘할 수 있다면 개별적으로 재정의하면 된다.
 */
public interface ReadStatusRepository extends CrudRepository<ReadStatus> {

    // 특정 사용자의 모든 읽음 상태를 조회 (사용자가 참여 중인 모든 채널의 읽음 상태)
    default List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .toList();
    }

    // 특정 채널의 모든 읽음 상태를 조회 (해당 채널에 참여 중인 모든 사용자의 읽음 상태)
    default List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(status -> status.getChannelId().equals(channelId))
                .toList();
    }

    // 특정 사용자 + 특정 채널 조합의 읽음 상태를 조회 (1:1 매핑)
    default Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .filter(status -> status.getChannelId().equals(channelId))
                .findFirst();
    }

    // 특정 사용자의 모든 읽음 상태를 삭제 (사용자 탈퇴 시 사용)
    default void deleteAllByUserId(UUID userId) {
        findAllByUserId(userId).forEach(status -> deleteById(status.getId()));
    }

    // 특정 채널의 모든 읽음 상태를 삭제 (채널 삭제 시 사용)
    default void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId).forEach(status -> deleteById(status.getId()));
    }
}
