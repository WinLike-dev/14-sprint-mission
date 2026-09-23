package com.sprint.mission.discodeit.channel.entity;

import com.sprint.mission.discodeit.common.exception.exceptions.InvalidValueException;
import com.sprint.mission.discodeit.common.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.channel.exception.UnsupportedChannelOperationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 채널 도메인 엔티티.
 * 디스코드의 "채널" 개념을 표현하며, PUBLIC(공개) 채널과 PRIVATE(비공개/DM) 채널 두 종류가 있다.
 * PUBLIC 채널은 이름과 설명을 가지고, PRIVATE 채널은 이름/설명 없이 참여자 목록으로만 구분된다.
 */
@Getter
@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 조회 결과를 담을 때 사용한다 * 학습용 테스트
public class Channel extends BaseUpdatableEntity {

    // channels 테이블의 컬럼 길이. @Column과 길이 검증이 같은 값을 쓴다.
    private static final int TYPE_MAX_LENGTH = 10;
    private static final int NAME_MAX_LENGTH = 100;
    private static final int DESCRIPTION_MAX_LENGTH = 500;

    @Enumerated(EnumType.STRING) // * 학습용 테스트
    @Column(nullable = false, updatable = false, length = TYPE_MAX_LENGTH)
    private ChannelType type; // 채널 유형 (PUBLIC 또는 PRIVATE), setter가 없어 한번 정해지면 변경 불가

    @Column(length = NAME_MAX_LENGTH)
    private String name; // 채널 이름 (PUBLIC만 사용, PRIVATE은 null)

    @Column(length = DESCRIPTION_MAX_LENGTH)
    private String description; // 채널 설명 (PUBLIC만 사용, PRIVATE은 null)

    // 새 채널을 처음 만들 때 사용하는 생성자 (ID와 시각은 저장 시 BaseEntity가 부여)
    private Channel(ChannelType type, String name, String description) {
        this.type = Objects.requireNonNull(type, "type은 null일 수 없습니다.");
        validateFields(type, name, description);
        this.name = name;
        this.description = description;
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
        requireMaxLength(name, NAME_MAX_LENGTH, "name");
        requireMaxLength(description, DESCRIPTION_MAX_LENGTH, "description");
    }

    // 컬럼 길이를 넘는 값은 insert/update가 DB에서 실패해 500이 되므로, 엔티티가 먼저 400으로 거절한다.
    private static void requireMaxLength(String value, int maxLength, String fieldName) {
        if (value.length() > maxLength) {
            throw new InvalidValueException(fieldName + "은(는) " + maxLength + "자를 넘을 수 없습니다.");
        }
    }
}
