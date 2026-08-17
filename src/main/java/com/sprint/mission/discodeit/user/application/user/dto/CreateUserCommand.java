package com.sprint.mission.discodeit.user.application.user.dto;

/**
 * 사용자 생성 유스케이스 입력.
 * REST UserCreateRequest를 그대로 쓰지 않는다.
 * 컨트롤러 JSON 모양과 유스케이스 입력을 나누기 위한 애플리케이션 DTO다.
 */
public record CreateUserCommand(
        String username,
        String email,
        String password,
        UserProfileCommand profile
) {
}
