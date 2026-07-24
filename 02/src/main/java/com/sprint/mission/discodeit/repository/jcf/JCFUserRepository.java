package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.CrudRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class JCFUserRepository implements CrudRepository<User> {

    private final JCFObjectStore<User> store;

    public JCFUserRepository() {
        this(new JCFObjectStore<>("사용자"));
    }

    public JCFUserRepository(JCFObjectStore<User> store) {
        this.store = Objects.requireNonNull(store);
    }

    @Override
    public User create(User user) {
        return store.create(user);
    }

    @Override
    public User findById(UUID id) {
        return store.findById(id);
    }

    @Override
    public List<User> findAll() {
        return store.findAll();
    }

    @Override
    public User update(User user) {
        return store.update(user);
    }

    @Override
    public void deleteById(UUID id) {
        store.deleteById(id);
    }
}
