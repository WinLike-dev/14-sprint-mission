package com.sprint.mission.discodeit.repository.objectStore;

import com.sprint.mission.discodeit.entity.Identifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class JCFObjectStore<T extends Identifiable>
        implements ObjectStore<T> {

    private final Map<UUID, T> data;
    private final UnaryOperator<T> copier;

    public JCFObjectStore(UnaryOperator<T> copier) {
        this.data = new HashMap<>();
        this.copier = Objects.requireNonNull(copier);
    }

    @Override
    public void write(T entity) {
        data.put(entity.getId(), copy(entity));
    }

    @Override
    public Optional<T> read(UUID id) {
        return Optional.ofNullable(data.get(id))
                .map(this::copy);
    }

    @Override
    public List<T> read() {
        List<T> copies = new ArrayList<>();
        for (T entity : data.values()) {
            copies.add(copy(entity));
        }
        return copies;
    }

    @Override
    public void remove(UUID id) {
        data.remove(id);
    }

    private T copy(T entity) {
        return Objects.requireNonNull(
                copier.apply(entity),
                "복사 결과는 null일 수 없습니다."
        );
    }
}
