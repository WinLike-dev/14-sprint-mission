package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.CrudRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BasicUserService implements UserService {

    // 설계: 비즈니스 서비스는 저장 기술이 아닌 Repository 계약에만 의존한다.
    private final CrudRepository<User> userRepository;

    public BasicUserService(CrudRepository<User> userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public User createUser(String username, String email, String password) {
        User user = new User(username, email, password);
        return userRepository.create(user);
    }

    @Override
    public User readUser(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> readAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateUser(UUID id, String username, String email, String password) {
        User user = userRepository.findById(id);
        user.update(username, email, password);
        userRepository.update(user);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
