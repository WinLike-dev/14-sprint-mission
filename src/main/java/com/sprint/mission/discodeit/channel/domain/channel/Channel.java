package com.sprint.mission.discodeit.channel.domain.channel;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import com.sprint.mission.discodeit.channel.domain.channel.exception.UnsupportedChannelOperationException;
import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 채널 도메인 엔티티.
 * 디스코드의 "채널" 개념을 표현하며, PUBLIC(공개) 채널과 PRIVATE(비공개/DM) 채널 두 종류가 있다.
 * PUBLIC 채널은 이름과 설명을 가지고, PRIVATE 채널은 이름/설명 없이 참여자 목록으로만 구분된다.
 */
@Getter
public class Channel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L; // 직렬화 버전 관리용 ID (JCF 파일 저장 시 필요)

    private final ChannelType type; // 채널 유형 (PUBLIC 또는 PRIVATE), 한번 정해지면 변경 불가
    private String name; // 채널 이름 (PUBLIC만 사용, PRIVATE은 null)
    private String description; // 채널 설명 (PUBLIC만 사용, PRIVATE은 null)
    private Instant lastMessageAt; // 이 채널에 마지막으로 메시지가 작성된 시각

    // 새 채널을 처음 만들 때 사용하는 생성자 (ID와 시각은 BaseEntity에서 자동 생성)
    private Channel(ChannelType type, String name, String description) {
        this.type = Objects.requireNonNull(type, "type은 null일 수 없습니다.");
        validateFields(type, name, description);
        this.name = name;
        this.description = description;
        this.lastMessageAt = null;
    }

    // 기존 데이터를 복원할 때 사용하는 생성자 (copy 메서드에서 호출)
    private Channel(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            ChannelType type,
            String name,
            String description,
            Instant lastMessageAt
    ) {
        super(id, createdAt, updatedAt);
        this.type = type;
        this.name = name;
        this.description = description;
        this.lastMessageAt = lastMessageAt;
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
        markUpdated();
    }

    // 채널의 마지막 메시지 시각을 갱신한다 (메시지가 생성/삭제될 때 호출됨)
    public void updateLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
        markUpdated();
    }

    // JCF 객체 카피를 위한 오퍼레이터
    public Channel copy() {
        return new Channel(
                getId(),
                getCreatedAt(),
                getUpdatedAt(),
                type,
                name,
                description,
                lastMessageAt
        );
    }

    // PRIVATE는 DM이므로 이름과 채널 설명 필요 없음
    private static void validateFields(ChannelType type, String name, String description) {
        if (type == ChannelType.PRIVATE) {
            if (name != null || description != null) {
                throw new IllegalArgumentException("PRIVATE 채널은 name과 description을 가질 수 없습니다.");
            }
            return;
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("PUBLIC 채널의 name은 비어 있을 수 없습니다.");
        }
        Objects.requireNonNull(description, "PUBLIC 채널의 description은 null일 수 없습니다.");
    }
}
