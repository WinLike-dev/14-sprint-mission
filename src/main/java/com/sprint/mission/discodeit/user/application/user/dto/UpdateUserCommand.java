package com.sprint.mission.discodeit.user.application.user.dto;

/**
 * 사용자 수정 유스케이스 입력.
 * REST UserUpdateRequest와 분리한다. null 필드는 기존 값 유지 규칙의 입구다.
 */
public record UpdateUserCommand(
        String username,
        String email,
        String password,
        UserProfileCommand profile
) {
}
