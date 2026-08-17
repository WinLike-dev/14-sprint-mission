package com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request;

/**
 * 사용자 생성 요청 DTO.
 * 클라이언트가 POST /api/users 호출 시 보내는 JSON 본문을 이 record로 매핑한다.
 * record는 불변 객체로, 한번 생성되면 값을 바꿀 수 없어 안전하다.
 */
public record UserCreateRequest(String username, String email, String password) {
}
