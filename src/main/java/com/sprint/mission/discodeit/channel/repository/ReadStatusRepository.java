package com.sprint.mission.discodeit.channel.repository;

import com.sprint.mission.discodeit.channel.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 읽음 상태 저장소.
 * 기본 CRUD 외에 사용자/채널 기준 조회·삭제를 메서드 이름 기반 쿼리로 제공한다.
 * 메서드 이름의 userId, channelId는 Spring Data가 user.id, channel.id 경로로 해석하므로
 * 연관관계로 바뀐 뒤에도 이름을 그대로 쓴다.
 */
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    // 특정 사용자의 모든 읽음 상태를 조회 (사용자가 참여 중인 모든 채널의 읽음 상태)
    List<ReadStatus> findAllByUserId(UUID userId);

    // 사용자가 참여 중인 채널 id만 조회한다.
    // 참여 여부 판별에는 읽음 상태 전체가 필요 없으므로 엔티티를 만들지 않는다.
    @Query("select r.channel.id from ReadStatus r where r.user.id = :userId")
    List<UUID> findChannelIdsByUserId(@Param("userId") UUID userId);

    // 여러 채널의 참여자 id를 한 번에 조회한다.
    // 채널마다 참여자를 물으면 채널 목록 한 번이 조회 N회로 늘어난다(N+1).
    @Query("""
            select r.channel.id as channelId, r.user.id as userId
            from ReadStatus r
            where r.channel.id in :channelIds
            """)
    List<ChannelParticipant> findParticipantsByChannelIdIn(@Param("channelIds") Collection<UUID> channelIds);

    // 특정 사용자 + 특정 채널 조합의 읽음 상태를 조회 (1:1 매핑)
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    // 해당 조합의 읽음 상태가 이미 있는지만 확인한다.
    // 중복 등록 검사처럼 객체가 아니라 존재 여부만 필요한 경우를 위한 메서드다.
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    // 이름 기반 삭제는 대상을 모두 조회한 뒤 한 건씩 DELETE한다.
    // 참여 채널이나 참여자가 많으면 그만큼 쿼리가 늘어나므로 벌크 연산으로 한 번에 지운다.
    // flushAutomatically: 쌓여 있던 변경을 DB에 먼저 반영해 벌크 DELETE가 최신 상태 위에서 실행되게 한다.
    // 부르는 쪽(UserServiceImpl.delete, ChannelServiceImpl.delete)이 @Transactional이다.

    // 특정 사용자의 모든 읽음 상태를 삭제 (사용자 탈퇴 시 사용)
    @Modifying(flushAutomatically = true)
    @Query("delete from ReadStatus r where r.user.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    // 특정 채널의 모든 읽음 상태를 삭제 (채널 삭제 시 사용)
    @Modifying(flushAutomatically = true)
    @Query("delete from ReadStatus r where r.channel.id = :channelId")
    void deleteAllByChannelId(@Param("channelId") UUID channelId);

    // 채널별 참여자 조회 결과
    interface ChannelParticipant {

        UUID getChannelId();

        UUID getUserId();
    }
}
