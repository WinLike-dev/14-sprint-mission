package com.sprint.mission.discodeit.channel.domain.readstatus;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import lombok.Getter;

import java.io.Serial;
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
public class ReadStatus extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L; // 직렬화 버전 관리용 ID

    private final UUID userId; // 이 읽음 상태의 대상 사용자 ID
    private final UUID channelId; // 이 읽음 상태가 연결된 채널 ID
    private Instant lastReadAt; // 사용자가 이 채널을 마지막으로 읽은 시각

    // 새로운 읽음 상태를 생성할 때 사용 (lastReadAt은 현재 시각으로 초기화)
    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.userId = Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        this.channelId = Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        this.lastReadAt = Objects.requireNonNull(lastReadAt, "lastReadAt은 null일 수 없습니다.");
    }

    // 기존 데이터를 복원할 때 사용하는 생성자 (copy 메서드에서 호출)
    private ReadStatus(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {
        super(id, createdAt, updatedAt);
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    // 설계: 읽은 시각은 외부 입력이 아니라 서버의 현재 시각으로 갱신한다.
    // 읽은 시각은 클라이언트가 알려준다. 서버 시각으로 덮어쓰지 않는다.
    public void updateLastReadAt(Instant newLastReadAt) {
        this.lastReadAt = Objects.requireNonNull(newLastReadAt, "newLastReadAt은 null일 수 없습니다.");
        markUpdated();
    }

    // 이것도 JCF를 위해
    public ReadStatus copy() {
        return new ReadStatus(
                getId(), getCreatedAt(), getUpdatedAt(), userId, channelId, lastReadAt
        );
    }
}
