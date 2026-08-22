package com.sprint.mission.discodeit.common.repository.jcf;

import com.sprint.mission.discodeit.common.entity.Identifiable;
import com.sprint.mission.discodeit.common.repository.AbstractCrudRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

public abstract class AbstractJCFRepository<T extends Identifiable>
        extends AbstractCrudRepository<T> {

    private final Map<UUID, T> data = new HashMap<>();
    private final UnaryOperator<T> copier;

    protected AbstractJCFRepository(Class<T> entityType, UnaryOperator<T> copier) {
        super(entityType);
        this.copier = Objects.requireNonNull(copier);
    }

    @Override
    protected final void write(T entity) {
        data.put(entity.getId(), copy(entity));
    }

    @Override
    protected final Optional<T> read(UUID id) {
        return Optional.ofNullable(data.get(id))
                .map(this::copy);
    }

    // 키 존재만 확인한다. 복사본을 만들지 않는다.
    @Override
    protected final boolean contains(UUID id) {
        return data.containsKey(id);
    }

    @Override
    protected final List<T> readAll() {
        List<T> copies = new ArrayList<>();
        for (T entity : data.values()) {
            copies.add(copy(entity));
        }
        return copies;
    }

    @Override
    protected final void remove(UUID id) {
        data.remove(id);
    }

    // JCF는 객체 자체를 메모리에 저장하기 때문에 레포지토리와 서비스가 같은 객체를 바라보게됨
    // 이는 FILE 레포지토리와 작동 불일치
    private T copy(T entity) {
        return Objects.requireNonNull(
                copier.apply(entity),
                "복사 결과는 null일 수 없습니다."
        );
    }
}
