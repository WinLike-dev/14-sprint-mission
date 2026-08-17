package com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.response;

import com.sprint.mission.discodeit.user.domain.status.UserStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 온라인 상태 응답 DTO.
 * 사용자 ID, 마지막 활동 시각, 현재 온라인 여부를 클라이언트에 반환한다.
 */
public record UserStatusDto(UUID userId, Instant lastActiveAt, boolean online) {

    // UserStatus 엔티티로부터 DTO를 생성하는 팩토리 메서드
    public static UserStatusDto from(UserStatus status) {
        return new UserStatusDto(status.getUserId(), status.getLastActiveAt(), status.isOnline());
    }
}
