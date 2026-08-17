package com.sprint.mission.discodeit.message.application.port.out;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.message.domain.message.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 메시지 저장소 outbound 포트.
 * 채널 ID 기준 조회/삭제 등 메시지 전용 쿼리를 추가로 정의한다.
 */
public interface MessageRepository extends CrudRepository<Message> {

    // 특정 채널에 속한 모든 메시지를 조회한다
    List<Message> findAllByChannelId(UUID channelId);

    // 특정 채널에서 가장 최근에 작성된 메시지를 조회한다 (채널의 마지막 활동 시각 계산 등에 사용)
    Optional<Message> findLatestByChannelId(UUID channelId);

    // 특정 채널에 속한 모든 메시지를 일괄 삭제한다 (채널 삭제 시 사용)
    void deleteAllByChannelId(UUID channelId);
}
