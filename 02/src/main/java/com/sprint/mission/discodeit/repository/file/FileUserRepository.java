package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.CrudRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileUserRepository implements CrudRepository<User> {

    // 설계: 상속 대신 파일 저장 객체를 소유하고 CRUD 호출을 위임한다.
    private final FileObjectStore<User> store;

    public FileUserRepository() {
        this(
                new FileObjectStore<>(
                        Path.of("data", "users"),
                        User.class,
                        "사용자"
                )
        );
    }

    public FileUserRepository(FileObjectStore<User> store) {
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
