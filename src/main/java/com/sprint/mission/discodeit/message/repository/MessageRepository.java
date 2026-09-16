package com.sprint.mission.discodeit.message.repository;

import com.sprint.mission.discodeit.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 메시지 저장소.
 * 채널 ID 기준 조회 등 메시지 전용 쿼리를 정의한다.
 */
public interface MessageRepository extends JpaRepository<Message, UUID> {

    // 특정 채널에 속한 모든 메시지를 조회한다
    List<Message> findAllByChannelId(UUID channelId);

    // 여러 채널의 마지막 메시지 시각을 한 번의 쿼리로 구한다.
    // 채널마다 따로 조회하면 채널 목록 한 번에 쿼리가 채널 수만큼 늘어나기 때문이다(N+1).
    // 메시지가 없는 채널은 결과에 포함되지 않는다.
    @Query("""
            select m.channel.id as channelId, max(m.createdAt) as lastMessageAt
            from Message m
            where m.channel.id in :channelIds
            group by m.channel.id
            """)
    List<ChannelLastMessageAt> findLastMessageAtByChannelIdIn(@Param("channelIds") Collection<UUID> channelIds);

    // 작성자가 탈퇴하면 그가 쓴 메시지의 작성자만 비운다. 메시지 본문은 남는다.
    // 한 건씩 UPDATE하지 않도록 벌크 연산으로 처리한다.
    // flushAutomatically: 쌓여 있던 변경을 DB에 먼저 반영해 벌크 UPDATE가 최신 상태 위에서 실행되게 한다.
    @Modifying(flushAutomatically = true)
    @Query("update Message m set m.author = null where m.author.id = :authorId")
    void detachAuthor(@Param("authorId") UUID authorId);

    // 채널별 마지막 메시지 시각 조회 결과
    interface ChannelLastMessageAt {

        UUID getChannelId();

        Instant getLastMessageAt();
    }
}
