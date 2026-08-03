package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class User implements Identifiable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        this(
                UUID.randomUUID(),
                System.currentTimeMillis(),
                null,
                username,
                email,
                password
        );
    }

    public void update(String username, String email, String password) {
        String validatedUsername = requireNonBlank(username, "username");
        String validatedEmail = requireNonBlank(email, "email");
        String validatedPassword = requireNonBlank(password, "password");

        this.username = validatedUsername;
        this.email = validatedEmail;
        this.password = validatedPassword;
        this.updatedAt = System.currentTimeMillis();
    }

    public User copy() {
        return new User(
                id,
                createdAt,
                updatedAt,
                username,
                email,
                password
        );
    }

    private User(
            UUID id,
            Long createdAt,
            Long updatedAt,
            String username,
            String email,
            String password
    ) {
        this.id = Objects.requireNonNull(id, "id는 null일 수 없습니다.");
        this.createdAt = Objects.requireNonNull(
                createdAt,
                "createdAt은 null일 수 없습니다."
        );
        this.updatedAt = updatedAt;
        this.username = requireNonBlank(username, "username");
        this.email = requireNonBlank(email, "email");
        this.password = requireNonBlank(password, "password");
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + "은(는) 비어 있을 수 없습니다."
            );
        }
        return value;
    }
}
