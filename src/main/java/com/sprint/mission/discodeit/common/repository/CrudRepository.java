package com.sprint.mission.discodeit.common.repository;

import com.sprint.mission.discodeit.common.entity.Identifiable;

import java.util.List;
import java.util.UUID;

// 설계: 인터페이스가 저장 계약을 소유하므로 Service는 File/JCF 구현 선택을 알 필요가 없다.
// 문법: T는 Identifiable 구현 타입만 받을 수 있는 제네릭 타입 매개변수다.
public interface CrudRepository<T extends Identifiable> {

    T create(T entity);

    T findById(UUID id);

    boolean existsById(UUID id);

    List<T> findAll();

    T update(T entity);

    void deleteById(UUID id);
}
