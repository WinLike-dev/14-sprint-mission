package com.sprint.mission.discodeit.common.repository;

import com.sprint.mission.discodeit.common.entity.Identifiable;
import com.sprint.mission.discodeit.common.exception.DuplicateEntityException;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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

        ensureNotStored(id);
        write(target);
        return target;
    }

    @Override
    public final T getById(UUID id) {
        return requireStored(Objects.requireNonNull(id));
    }

    @Override
    public final boolean existsById(UUID id) {
        return read(Objects.requireNonNull(id)).isPresent();
    }

    @Override
    public final List<T> findAll() {
        return List.copyOf(readAll());
    }

    @Override
    public final T update(T entity) {
        T target = Objects.requireNonNull(entity);
        UUID id = Objects.requireNonNull(target.getId());

        requireStored(id);
        write(target);
        return target;
    }

    @Override
    public final void deleteById(UUID id) {
        UUID targetId = Objects.requireNonNull(id);

        requireStored(targetId);
        remove(targetId);
    }

    private void ensureNotStored(UUID id) {
        if (read(id).isPresent()) {
            throw new DuplicateEntityException(entityType, id);
        }
    }

    private T requireStored(UUID id) {
        return read(id).orElseThrow(
                () -> new EntityNotFoundException(entityType, id)
        );
    }

    protected abstract void write(T entity);

    protected abstract Optional<T> read(UUID id);

    protected abstract List<T> readAll();

    protected abstract void remove(UUID id);
}
