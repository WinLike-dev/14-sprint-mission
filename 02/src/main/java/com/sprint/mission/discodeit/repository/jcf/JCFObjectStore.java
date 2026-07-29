package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Identifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

// 설계: JCF 저장 기술만 책임지며 구체 엔티티와 서비스 계층을 알지 못한다.
public final class JCFObjectStore<T extends Identifiable> {

    private final Map<UUID, T> data;
    private final String entityName;

    public JCFObjectStore(String entityName) {
        this.data = new HashMap<>();
        this.entityName = Objects.requireNonNull(entityName);
    }

    public T create(T entity) {
        UUID id = entity.getId();

        if (data.containsKey(id)) {
            throw new IllegalStateException(
                    "이미 존재하는 " + entityName + "입니다: " + id
            );
        }

        data.put(id, entity);
        return entity;
    }

    public T findById(UUID id) {
        T entity = data.get(id);

        if (entity == null) {
            throw new IllegalStateException(
                    entityName + " 데이터를 찾을 수 없습니다: " + id
            );
        }

        return entity;
    }

    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    public T update(T entity) {
        UUID id = entity.getId();

        findById(id);
        data.put(id, entity);

        return entity;
    }

    public void deleteById(UUID id) {
        findById(id);
        data.remove(id);
    }
}
