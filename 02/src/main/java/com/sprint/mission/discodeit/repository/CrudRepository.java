package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

// 문법: 제네릭 인터페이스는 엔티티 타입과 관계없이 동일한 CRUD 계약을 제공한다.
public interface CrudRepository<T> {

    T create(T entity);

    T findById(UUID id);

    List<T> findAll();

    T update(T entity);

    void deleteById(UUID id);
}
