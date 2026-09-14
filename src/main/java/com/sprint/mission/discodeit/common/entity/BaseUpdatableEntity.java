package com.sprint.mission.discodeit.common.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.Instant;

/**
 * 수정 가능한 엔티티의 공통 부모 클래스(추상 클래스).
 * BaseEntity의 id, createdAt에 수정시각(updatedAt)을 더한다.
 * updatedAt도 저장 시점에 채워지므로 저장 전에는 null이다.
 */
@Getter
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {

    private Instant updatedAt;       // 엔티티가 마지막으로 수정된 시각 (저장 후에는 항상 값이 있다)

    // 처음 저장될 때는 수정 이력이 없으므로 createdAt과 같은 값으로 시작한다.
    // 부모의 @PrePersist가 먼저 실행되므로 createdAt이 이미 채워져 있다.
    @PrePersist
    private void initUpdatedAt() {
        updatedAt = getCreatedAt();
    }

    // 변경 감지로 UPDATE가 나갈 때(flush 시점) 수정 시각을 갱신한다.
    // 값이 실제로 바뀌지 않으면 UPDATE가 없으므로 호출되지 않는다.
    @PreUpdate
    private void touchUpdatedAt() {
        updatedAt = Instant.now();
    }
}
