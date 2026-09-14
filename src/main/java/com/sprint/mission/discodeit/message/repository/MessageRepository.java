package com.sprint.mission.discodeit.message.repository;

import com.sprint.mission.discodeit.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 메시지 저장소 outbound 포트.
 * 채널 ID 기준 조회 등 메시지 전용 쿼리를 메서드 이름 기반 쿼리로 정의한다.
 */
public interface MessageRepository extends JpaRepository<Message, UUID> {

    // 특정 채널에 속한 모든 메시지를 조회한다
    List<Message> findAllByChannelId(UUID channelId);

    // 특정 채널에서 가장 최근에 작성된 메시지를 조회한다 (채널의 마지막 활동 시각 계산 등에 사용)
    Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);
}
