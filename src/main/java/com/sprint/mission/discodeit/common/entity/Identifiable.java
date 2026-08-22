package com.sprint.mission.discodeit.common.entity;

import java.util.UUID;

/**
 * 고유 식별자(ID)를 가지는 객체임을 나타내는 인터페이스.
 * 이 인터페이스를 구현하면 해당 객체가 UUID 기반의 고유 ID를 갖고 있음을 보장한다.
 * 주로 엔티티(Entity) 클래스들이 구현하며, 리포지토리에서 제네릭 타입 제약으로도 사용된다.
 */
public interface Identifiable {

    // 이 객체의 고유 식별자(UUID)를 반환한다
    UUID getId();
}
