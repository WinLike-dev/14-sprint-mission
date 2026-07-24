package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.CrudRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileChannelRepository implements CrudRepository<Channel> {

    private final FileObjectStore<Channel> store;

    public FileChannelRepository() {
        this(
                new FileObjectStore<>(
                        Path.of("data", "channels"),
                        Channel.class,
                        "채널"
                )
        );
    }

    public FileChannelRepository(FileObjectStore<Channel> store) {
        this.store = Objects.requireNonNull(store);
    }

    @Override
    public Channel create(Channel channel) {
        return store.create(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return store.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return store.findAll();
    }

    @Override
    public Channel update(Channel channel) {
        return store.update(channel);
    }

    @Override
    public void deleteById(UUID id) {
        store.deleteById(id);
    }
}
