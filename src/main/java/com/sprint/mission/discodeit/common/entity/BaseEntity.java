package com.sprint.mission.discodeit.common.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * 모든 엔티티의 공통 부모 클래스(추상 클래스).
 * id, 생성시각(createdAt)을 공통으로 관리한다.
 * 두 값은 new로 객체를 만들 때가 아니라 처음 저장(persist)될 때 부여된다.
 * 따라서 저장 전의 엔티티는 id와 createdAt이 null이다.
 * 수정 가능한 엔티티는 updatedAt을 더한 BaseUpdatableEntity를 상속한다.
 */
@Getter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;          // 엔티티의 고유 식별자 (setter가 없어 저장 후 변경 불가)

    @Column(nullable = false, updatable = false)
    private Instant createdAt; // 엔티티가 처음 저장된 시각

    // 처음 저장될 때 생성 시각을 기록한다.
    // private인 이유: 하위 클래스가 같은 이름으로 오버라이드하면 이 콜백이 호출되지 않기 때문이다.
    @PrePersist
    private void initCreatedAt() {
        createdAt = Instant.now();
    }
}
