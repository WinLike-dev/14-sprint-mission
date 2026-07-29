package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.CrudRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileMessageRepository implements CrudRepository<Message> {

    private final FileObjectStore<Message> store;

    public FileMessageRepository() {
        this(
                new FileObjectStore<>(
                        Path.of("data", "messages"),
                        Message.class,
                        "메시지"
                )
        );
    }

    public FileMessageRepository(FileObjectStore<Message> store) {
        this.store = Objects.requireNonNull(store);
    }

    @Override
    public Message create(Message message) {
        return store.create(message);
    }

    @Override
    public Message findById(UUID id) {
        return store.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return store.findAll();
    }

    @Override
    public Message update(Message message) {
        return store.update(message);
    }

    @Override
    public void deleteById(UUID id) {
        store.deleteById(id);
    }
}
