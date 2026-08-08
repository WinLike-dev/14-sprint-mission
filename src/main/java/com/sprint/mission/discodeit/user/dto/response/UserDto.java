package com.sprint.mission.discodeit.user.dto.response;

import com.sprint.mission.discodeit.user.entity.User;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        boolean online
) {
    // 문법: static 팩토리 메서드는 DTO 생성 규칙을 객체 생성 지점 한 곳에 모은다.
    public static UserDto from(User user, boolean online) {
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online
        );
    }
}
