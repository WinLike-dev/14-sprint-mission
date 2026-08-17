package com.sprint.mission.discodeit.user.application.user.dto;

import com.sprint.mission.discodeit.user.domain.user.User;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 유스케이스가 밖으로 내보내는 결과.
 * User 엔티티를 그대로 노출하지 않는다. password가 있고,
 * 응답에 필요한 online은 User가 아니라 UserStatus에서 오기 때문이다.
 * REST UserDto는 이 결과를 HTTP 필드로만 옮긴다.
 */
public record UserResult(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        boolean online
) {
    public static UserResult from(User user, boolean online) {
        return new UserResult(
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
