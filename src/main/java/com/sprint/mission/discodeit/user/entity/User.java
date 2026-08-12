package com.sprint.mission.discodeit.user.entity;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
public class User extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 설계: 이메일 형식은 User가 항상 지켜야 하는 불변식이므로 Entity가 검증한다.
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private String username;
    private String email;
    private String password;
    private UUID profileId;

    public User(String username, String email, String password, UUID profileId) {
        this.username = requireNonBlank(username, "username");
        this.email = requireValidEmail(email);
        this.password = requireNonBlank(password, "password");
        this.profileId = profileId;
    }

    private User(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            String password,
            UUID profileId
    ) {
        super(id, createdAt, updatedAt);
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(String username, String email, String password, UUID profileId) {
        String nextUsername = username == null ? this.username : requireNonBlank(username, "username");
        String nextEmail = email == null ? this.email : requireValidEmail(email);
        String nextPassword = password == null ? this.password : requireNonBlank(password, "password");

        this.username = nextUsername;
        this.email = nextEmail;
        this.password = nextPassword;
        this.profileId = profileId;
        markUpdated();
    }

    public User copy() {
        return new User(
                getId(),
                getCreatedAt(),
                getUpdatedAt(),
                username,
                email,
                password,
                profileId
        );
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "은(는) 비어 있을 수 없습니다.");
        }
        return value;
    }

    private static String requireValidEmail(String email) {
        String value = requireNonBlank(email, "email");
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("email 형식이 올바르지 않습니다.");
        }
        return value;
    }
}
