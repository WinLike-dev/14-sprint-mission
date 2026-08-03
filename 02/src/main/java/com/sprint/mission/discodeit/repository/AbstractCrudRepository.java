package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Identifiable;
import com.sprint.mission.discodeit.exception.DuplicateEntityException;
import com.sprint.mission.discodeit.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.exception.StorageOperationException;
import com.sprint.mission.discodeit.repository.objectStore.ObjectStore;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        writeToStore(target, "create", id);

        return target;
    }

    public final T findById(UUID id) {
        return requireStored(Objects.requireNonNull(id), "findById");
    }

    public final boolean existsById(UUID id) {
        return readFromStore(
                Objects.requireNonNull(id),
                "existsById"
        ).isEmpty();
    }

    public final List<T> findAll() {
        return List.copyOf(readAllFromStore());
    }

    public final T update(T entity) {
        T target = Objects.requireNonNull(entity);
        UUID id = Objects.requireNonNull(target.getId());

        requireStored(id, "update");
        writeToStore(target, "update", id);

        return target;
    }

    public final void deleteById(UUID id) {
        UUID targetId = Objects.requireNonNull(id);

        requireStored(targetId, "deleteById");
        removeFromStore(targetId);
    }

    protected abstract Class<T> entityType();

    private void ensureNotStored(UUID id) {
        if (readFromStore(id, "create").isPresent()) {
            throw new DuplicateEntityException(entityType(), id);
        }
    }

    private T requireStored(UUID id, String operation) {
        return readFromStore(id, operation)
                .orElseThrow(
                        () -> new EntityNotFoundException(entityType(), id)
                );
    }

    private Optional<T> readFromStore(UUID id, String operation) {
        try {
            return objectStore.read(id);
        } catch (NoSuchFileException exception) {
            return Optional.empty();
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            throw new StorageOperationException(
                    operation,
                    entityType(),
                    id,
                    exception
            );
        }
    }

    private List<T> readAllFromStore() {
        try {
            return objectStore.read();
        } catch (NoSuchFileException exception) {
            return List.of();
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException |
                 DirectoryIteratorException exception) {
            throw new StorageOperationException(
                    "findAll",
                    entityType(),
                    null,
                    exception
            );
        }
    }

    private void writeToStore(T entity, String operation, UUID id) {
        try {
            objectStore.write(entity);
        } catch (IOException exception) {
            throw new StorageOperationException(
                    operation,
                    entityType(),
                    id,
                    exception
            );
        }
    }

    private void removeFromStore(UUID id) {
        try {
            objectStore.remove(id);
        } catch (NoSuchFileException exception) {
            throw new EntityNotFoundException(entityType(), id);
        } catch (IOException exception) {
            throw new StorageOperationException(
                    "deleteById",
                    entityType(),
                    id,
                    exception
            );
        }
    }
}
