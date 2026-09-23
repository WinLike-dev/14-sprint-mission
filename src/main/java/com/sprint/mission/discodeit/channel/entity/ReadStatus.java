package com.sprint.mission.discodeit.channel.entity;

import com.sprint.mission.discodeit.common.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

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

    // 부모 User를 가리키는 단방향 N:1. FK를 가진 쪽이라 LAZY가 실제로 동작한다.
    // 자식에서 부모로는 cascade를 걸지 않는다. 읽음 상태를 지운다고 사용자가 지워지면 안 되기 때문이다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // @ManyToOne 기본값은 EAGER라 LAZY로 명시한다 * 학습 테스트 (EAGER는 정말 N+1에 해결책이 못 되어 주나?)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user; // 이 읽음 상태의 대상 사용자

    // 부모 Channel을 가리키는 단방향 N:1.
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // * 학습 테스트 (User와 마찬가지로)
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    private Channel channel; // 이 읽음 상태가 연결된 채널

    @Column(nullable = false)
    private Instant lastReadAt; // 사용자가 이 채널을 마지막으로 읽은 시각

    // 새로운 읽음 상태를 생성할 때 사용 (lastReadAt은 호출자가 정한다)
    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = Objects.requireNonNull(user, "user는 null일 수 없습니다.");
        this.channel = Objects.requireNonNull(channel, "channel은 null일 수 없습니다.");
        this.lastReadAt = Objects.requireNonNull(lastReadAt, "lastReadAt은 null일 수 없습니다.");
    }

    // 읽은 시각은 클라이언트가 알려준다. 서버 시각으로 덮어쓰지 않는다.
    public void updateLastReadAt(Instant newLastReadAt) {
        this.lastReadAt = Objects.requireNonNull(newLastReadAt, "newLastReadAt은 null일 수 없습니다.");
    }
}
