package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.CrudRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserRepository implements CrudRepository<User> {

    private final Map<UUID, User> data;

    public JCFUserRepository() {
        data = new HashMap<>();
    }

    @Override
    public User create(User user) {
        if (data.containsKey(user.getId())) {
            throw new IllegalStateException(
                    "이미 존재하는 사용자입니다: " + user.getId()
            );
        }

        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalStateException(
                    "사용자 데이터를 찾을 수 없습니다: " + id
            );
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(User user) {
        findById(user.getId());
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        data.remove(id);
    }
}
