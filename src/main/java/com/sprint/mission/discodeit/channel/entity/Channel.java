package com.sprint.mission.discodeit.channel.entity;

import com.sprint.mission.discodeit.common.exception.InvalidValueException;
import com.sprint.mission.discodeit.common.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.channel.exception.UnsupportedChannelOperationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

/**
 * 채널 도메인 엔티티.
 * 디스코드의 "채널" 개념을 표현하며, PUBLIC(공개) 채널과 PRIVATE(비공개/DM) 채널 두 종류가 있다.
 * PUBLIC 채널은 이름과 설명을 가지고, PRIVATE 채널은 이름/설명 없이 참여자 목록으로만 구분된다.
 */
@Getter
@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 조회 결과를 담을 때 사용한다
public class Channel extends BaseUpdatableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 10)
    private ChannelType type; // 채널 유형 (PUBLIC 또는 PRIVATE), setter가 없어 한번 정해지면 변경 불가

    @Column(length = 100)
    private String name; // 채널 이름 (PUBLIC만 사용, PRIVATE은 null)

    @Column(length = 500)
    private String description; // 채널 설명 (PUBLIC만 사용, PRIVATE은 null)

    // ERD에 없는 값이라 매핑하지 않는다. 저장되지 않으므로 조회한 채널에서는 항상 null이다.
    // 메시지 저장소에서 최신 메시지 시각을 조회하는 방식으로 옮긴 뒤 필드를 제거한다.
    @Transient
    private Instant lastMessageAt; // 이 채널에 마지막으로 메시지가 작성된 시각

    // 새 채널을 처음 만들 때 사용하는 생성자 (ID와 시각은 저장 시 BaseEntity가 부여)
    private Channel(ChannelType type, String name, String description) {
        this.type = Objects.requireNonNull(type, "type은 null일 수 없습니다.");
        validateFields(type, name, description);
        this.name = name;
        this.description = description;
        this.lastMessageAt = null;
    }

    // 공개 채널을 생성하는 팩토리 메서드 (이름과 설명 필수)
    public static Channel publicChannel(String name, String description) {
        return new Channel(ChannelType.PUBLIC, name, description);
    }

    // 비공개(DM) 채널을 생성하는 팩토리 메서드 (이름/설명 없음)
    public static Channel privateChannel() {
        return new Channel(ChannelType.PRIVATE, null, null);
    }

    // PRIVATE는 DM이므로 이름이랑 설명이 없다. 따라서 지원되지 않는 예외 처리
    public void update(String name, String description) {
        if (type == ChannelType.PRIVATE) {
            throw new UnsupportedChannelOperationException(getId(), "update");
        }
        validateFields(type, name, description);
        this.name = name;
        this.description = description;
    }

    // 채널의 마지막 메시지 시각을 갱신한다 (메시지가 생성/삭제될 때 호출됨)
    public void updateLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    // PRIVATE는 DM이므로 이름과 채널 설명 필요 없음
    private static void validateFields(ChannelType type, String name, String description) {
        if (type == ChannelType.PRIVATE) {
            if (name != null || description != null) {
                throw new InvalidValueException("PRIVATE 채널은 name과 description을 가질 수 없습니다.");
            }
            return;
        }
        if (name == null || name.isBlank()) {
            throw new InvalidValueException("PUBLIC 채널의 name은 비어 있을 수 없습니다.");
        }
        Objects.requireNonNull(description, "PUBLIC 채널의 description은 null일 수 없습니다.");
    }
}
