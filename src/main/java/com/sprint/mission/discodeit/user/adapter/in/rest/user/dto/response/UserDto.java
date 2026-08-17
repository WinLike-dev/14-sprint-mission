package com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.response;

import com.sprint.mission.discodeit.user.domain.user.User;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 정보 응답 DTO.
 * User 엔티티의 핵심 정보와 온라인 여부를 합쳐서 클라이언트에 반환한다.
 * password는 포함하지 않아 보안을 유지한다.
 */
public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        boolean online       // 현재 온라인 상태인지 여부
) {
    // 문법: static 팩토리 메서드는 DTO 생성 규칙을 객체 생성 지점 한 곳에 모은다.
    // User 엔티티와 온라인 여부를 받아 DTO를 생성한다.
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
