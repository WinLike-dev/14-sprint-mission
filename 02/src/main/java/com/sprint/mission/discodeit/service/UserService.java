package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User createUser(String username, String email, String password);

    User loadUser(UUID id);

    List<User> loadAllUsers();

    void updateUser(UUID id, String username, String email, String password);

    void deleteUser(UUID id);
}
