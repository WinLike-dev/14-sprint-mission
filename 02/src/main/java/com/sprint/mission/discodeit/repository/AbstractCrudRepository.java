package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Identifiable;
import com.sprint.mission.discodeit.exception.DuplicateEntityException;
import com.sprint.mission.discodeit.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.repository.objectStore.ObjectStore;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractCrudRepository<T extends Identifiable> {

    private final ObjectStore<T> objectStore;

    protected AbstractCrudRepository(ObjectStore<T> objectStore) {
        this.objectStore = Objects.requireNonNull(objectStore);
    }

    public final T create(T entity) {
        T target = Objects.requireNonNull(entity);
        UUID id = Objects.requireNonNull(target.getId());

        ensureNotStored(id);
        objectStore.save(target);

        return target;
    }

    public final T findById(UUID id) {
        return requireStored(Objects.requireNonNull(id));
    }

    public final boolean existsById(UUID id) {
        return objectStore.load(Objects.requireNonNull(id)).isPresent();
    }

    public final List<T> findAll() {
        return List.copyOf(objectStore.load());
    }

    public final T update(T entity) {
        T target = Objects.requireNonNull(entity);
        UUID id = Objects.requireNonNull(target.getId());

        requireStored(id);
        objectStore.save(target);

        return target;
    }

    public final void deleteById(UUID id) {
        UUID targetId = Objects.requireNonNull(id);

        requireStored(targetId);
        objectStore.delete(targetId);
    }

    protected abstract Class<T> entityType();

    private void ensureNotStored(UUID id) {
        if (objectStore.load(id).isPresent()) {
            throw new DuplicateEntityException(entityType(), id);
        }
    }

    private T requireStored(UUID id) {
        return objectStore.load(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(entityType(), id)
                );
    }
}
