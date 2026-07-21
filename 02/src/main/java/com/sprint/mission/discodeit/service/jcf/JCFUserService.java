package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final Map<UUID, User> userMap;

    public JCFUserService() {
        userMap = new HashMap<>();
    }

    @Override
    public User createUser(String name, String email, String password) {
        User user = new User(name, email, password);
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User readUser(UUID id) {
        User user = userMap.get(id);

        if (user == null) {
            throw new NoSuchElementException("사용자를 찾을 수 없습니다: " + id);
        }
        return user;
    }

    @Override
    public List<User> readAllUsers() {
        return userMap.values().stream()
                .toList();
    }

    @Override
    public void updateUser(UUID id, String username, String email, String password) {
        User user = readUser(id);
        user.update(username, email, password);
    }

    @Override
    public void deleteUser(UUID id) {
        readUser(id);
        userMap.remove(id);
    }
}
