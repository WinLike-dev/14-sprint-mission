package com.sprint.mission.discodeit.channel.domain.readstatus;

import com.sprint.mission.discodeit.common.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 읽음 상태(ReadStatus) 도메인 엔티티.
 * 특정 사용자가 특정 채널의 메시지를 마지막으로 읽은 시각을 기록한다.
 * 이를 통해 "안 읽은 메시지"가 있는지 판단할 수 있다.
 * (채널의 lastMessageAt > ReadStatus의 lastReadAt 이면 안 읽은 메시지가 있는 것)
 */
@Getter
@Entity
@Table(
        name = "read_statuses",
        // 한 사용자는 한 채널에 읽음 상태를 하나만 가진다.
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 조회 결과를 담을 때 사용한다
public class ReadStatus extends BaseUpdatableEntity {

    // 연관관계 매핑 전까지는 FK 컬럼 값만 UUID로 다룬다.
    @Column(nullable = false, updatable = false)
    private UUID userId; // 이 읽음 상태의 대상 사용자 ID

    @Column(nullable = false, updatable = false)
    private UUID channelId; // 이 읽음 상태가 연결된 채널 ID

    @Column(nullable = false)
    private Instant lastReadAt; // 사용자가 이 채널을 마지막으로 읽은 시각

    // 새로운 읽음 상태를 생성할 때 사용 (lastReadAt은 호출자가 정한다)
    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.userId = Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        this.channelId = Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        this.lastReadAt = Objects.requireNonNull(lastReadAt, "lastReadAt은 null일 수 없습니다.");
    }

    // 읽은 시각은 클라이언트가 알려준다. 서버 시각으로 덮어쓰지 않는다.
    public void updateLastReadAt(Instant newLastReadAt) {
        this.lastReadAt = Objects.requireNonNull(newLastReadAt, "newLastReadAt은 null일 수 없습니다.");
    }
}
