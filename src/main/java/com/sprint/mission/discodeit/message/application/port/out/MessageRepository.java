package com.sprint.mission.discodeit.message.application.port.out;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.message.domain.message.Message;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 메시지 저장소 outbound 포트.
 * 채널 ID 기준 조회/삭제 등 메시지 전용 쿼리를 추가로 정의한다.
 *
 * 파생 조회는 저장 기술과 무관한 도메인 기준 필터링이므로 default로 한 번만 정의한다.
 * 구현체가 인덱스 등으로 더 잘할 수 있다면 개별적으로 재정의하면 된다.
 */
public interface MessageRepository extends CrudRepository<Message> {

    // 특정 채널에 속한 모든 메시지를 조회한다
    default List<Message> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    // 특정 채널에서 가장 최근에 작성된 메시지를 조회한다 (채널의 마지막 활동 시각 계산 등에 사용)
    default Optional<Message> findLatestByChannelId(UUID channelId) {
        return findAllByChannelId(channelId).stream()
                .max(Comparator.comparing(Message::getCreatedAt));
    }

    // 특정 채널에 속한 모든 메시지를 일괄 삭제한다 (채널 삭제 시 사용)
    default void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId).forEach(message -> deleteById(message.getId()));
    }
}
