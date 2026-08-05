package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

// Spring이 Service Bean을 찾고, Lombok이 final 의존성을 받는 생성자를 만든다.
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
    );

    private final UserRepository userRepository;

    @Override
    public User createUser(String username, String email, String password) {
        validateEmail(email);
        User user = new User(username, email, password);
        return userRepository.create(user);
    }

    @Override
    public User loadUser(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> loadAllUsers() {
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
