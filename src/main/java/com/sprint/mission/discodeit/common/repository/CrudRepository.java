package com.sprint.mission.discodeit.common.repository;

import com.sprint.mission.discodeit.common.entity.Identifiable;

import java.util.List;
import java.util.UUID;

// 설계: 인터페이스가 저장 계약을 소유하므로 Service는 File/JCF 구현 선택을 알 필요가 없다.
// 문법: T는 Identifiable 구현 타입만 받을 수 있는 제네릭 타입 매개변수다.
public interface CrudRepository<T extends Identifiable> {

    T create(T entity);

    // ID 단건 조회는 반드시 존재해야 하므로 get으로 필수 조회 계약을 드러낸다.
    // 들어오는 ID에 대해 계약 상 존재 보장 조회를 보장했음을 정의했다 가정
    T getById(UUID id);

    boolean existsById(UUID id);

    List<T> findAll();

    T update(T entity);

    void deleteById(UUID id);
}
