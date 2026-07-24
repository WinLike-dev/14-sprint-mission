package com.sprint.mission.discodeit.entity;

import java.util.UUID;

// 설계: 저장 객체가 구체 엔티티를 몰라도 공통 식별자를 사용할 수 있게 한다.
public interface Identifiable {

    UUID getId();
}
