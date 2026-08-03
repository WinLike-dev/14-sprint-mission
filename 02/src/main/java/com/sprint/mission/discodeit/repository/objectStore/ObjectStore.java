package com.sprint.mission.discodeit.repository.objectStore;

import com.sprint.mission.discodeit.entity.Identifiable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObjectStore<T extends Identifiable> {

    void write(T entity) throws IOException;

    Optional<T> read(UUID id) throws IOException, ClassNotFoundException;

    List<T> read() throws IOException, ClassNotFoundException;

    void remove(UUID id) throws IOException;
}
