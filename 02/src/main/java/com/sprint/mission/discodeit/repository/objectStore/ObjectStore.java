package com.sprint.mission.discodeit.repository.objectStore;

import com.sprint.mission.discodeit.entity.Identifiable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObjectStore<T extends Identifiable> {

    void save(T entity);

    Optional<T> load(UUID id);

    List<T> load();

    void delete(UUID id);
}
