package com.sprint.mission.discodeit.common.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 모든 엔티티의 공통 부모 클래스(추상 클래스).
 * id, 생성시각(createdAt), 수정시각(updatedAt)을 공통으로 관리한다.
 * 새로운 엔티티를 만들 때 자동으로 UUID와 생성시각이 부여되므로,
 * 하위 클래스에서는 비즈니스 필드만 신경 쓰면 된다.
 *
 * Serializable을 구현하는 이유: 파일 기반 저장소에서 객체를 직렬화(파일로 저장)하기 위함이다.
 */
@Getter
public abstract class BaseEntity implements Identifiable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // 직렬화 버전 관리용 상수

    private final UUID id;          // 엔티티의 고유 식별자 (한 번 생성되면 변경 불가)
    private final Instant createdAt; // 엔티티가 처음 생성된 시각
    private Instant updatedAt;       // 엔티티가 마지막으로 수정된 시각 (수정 전까지는 null)

    // 기본 생성자: 새 엔티티를 만들 때 사용. UUID와 현재 시각이 자동 부여된다.
    protected BaseEntity() {
        this(UUID.randomUUID(), Instant.now(), null);
    }

    // 모든 필드를 직접 지정하는 생성자: 기존 데이터를 복원할 때(역직렬화 등) 사용된다.
    protected BaseEntity(UUID id, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id는 null일 수 없습니다.");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt은 null일 수 없습니다.");
        this.updatedAt = updatedAt;
    }

    // 엔티티가 수정되었을 때 호출하여 updatedAt을 현재 시각으로 갱신한다.
    // final로 선언한 이유: 하위 클래스에서 이 동작을 임의로 변경하지 못하게 하기 위함이다.
    protected final void markUpdated() {
        updatedAt = Instant.now();
    }
}
