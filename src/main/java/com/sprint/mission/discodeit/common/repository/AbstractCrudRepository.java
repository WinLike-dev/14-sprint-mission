package com.sprint.mission.discodeit.common.repository;

import com.sprint.mission.discodeit.common.entity.Identifiable;
import com.sprint.mission.discodeit.common.exception.DuplicateEntityException;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class AbstractCrudRepository<T extends Identifiable>
        implements CrudRepository<T> {

    private final Class<T> entityType;

    protected AbstractCrudRepository(Class<T> entityType) {
        this.entityType = Objects.requireNonNull(entityType);
    }

    @Override
    public final T create(T entity) {
        T target = Objects.requireNonNull(entity);
        UUID id = Objects.requireNonNull(target.getId());

        return inEntityLock(id, () -> {
            ensureNotStored(id);
            write(target);
            return target;
        });
    }

    @Override
    public final T getById(UUID id) {
        UUID targetId = Objects.requireNonNull(id);
        return inEntityLock(targetId, () -> requireStored(targetId));
    }

    @Override
    public final boolean existsById(UUID id) {
        return contains(Objects.requireNonNull(id));
    }

    @Override
    public final List<T> findAll() {
        return List.copyOf(readAll());
    }

    @Override
    public final T update(T entity) {
        T target = Objects.requireNonNull(entity);
        UUID id = Objects.requireNonNull(target.getId());

        return inEntityLock(id, () -> {
            ensureStored(id);
            write(target);
            return target;
        });
    }

    @Override
    public final void deleteById(UUID id) {
        UUID targetId = Objects.requireNonNull(id);

        inEntityLock(targetId, () -> {
            ensureStored(targetId);
            remove(targetId);
            return null;
        });
    }

    // 존재 확인만 필요한 경로는 객체를 읽지 않는다.
    // 예전에는 read(id)로 확인했는데, File은 파일 전체를 역직렬화하고 JCF는 객체를 통째로
    // 복사해서 boolean 하나를 얻었다. 첨부 바이트를 가진 엔티티라면 비용이 더 커진다.
    private void ensureNotStored(UUID id) {
        if (contains(id)) {
            throw new DuplicateEntityException(entityType, id);
        }
    }

    private void ensureStored(UUID id) {
        if (!contains(id)) {
            throw new EntityNotFoundException(entityType, id);
        }
    }

    private T requireStored(UUID id) {
        return read(id).orElseThrow(
                () -> new EntityNotFoundException(entityType, id)
        );
    }

    // 한 엔티티에 대한 확인과 반영을 하나의 단위로 묶는 지점.
    // 저장 기술이 동시 접근을 어떻게 막을지는 구현체가 정한다.
    // 기본값은 아무것도 하지 않는다. 잠금이 필요한 구현체만 이 메서드를 재정의한다.
    protected <R> R inEntityLock(UUID id, Supplier<R> action) {
        return action.get();
    }

    protected abstract void write(T entity);

    protected abstract Optional<T> read(UUID id);

    // 엔티티를 만들지 않고 존재 여부만 판단한다. 구현체가 가장 싼 방법으로 답한다.
    protected abstract boolean contains(UUID id);

    protected abstract List<T> readAll();

    protected abstract void remove(UUID id);
}
