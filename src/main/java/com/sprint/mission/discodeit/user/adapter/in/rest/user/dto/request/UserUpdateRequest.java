package com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request;

/**
 * 사용자 수정 요청 DTO.
 * 클라이언트가 PUT /api/users/{id} 호출 시 보내는 JSON 본문을 이 record로 매핑한다.
 * 변경하지 않을 필드는 null로 보내면 기존 값이 유지된다.
 */
public record UserUpdateRequest(String username, String email, String password) {
}
