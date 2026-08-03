package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class BasicUserService implements UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
    );

    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public User createUser(String username, String email, String password) {
        validateEmail(email);
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
        validateEmail(email);
        User user = userRepository.findById(id);
        user.update(username, email, password);
        userRepository.update(user);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(
                    "email 형식이 올바르지 않습니다."
            );
        }
    }
}
