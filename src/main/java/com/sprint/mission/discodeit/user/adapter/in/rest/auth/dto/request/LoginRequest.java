package com.sprint.mission.discodeit.user.adapter.in.rest.auth.dto.request;

/**
 * 로그인 요청 DTO.
 * 클라이언트가 POST /api/auth/login 호출 시 보내는 JSON 본문을 이 record로 매핑한다.
 * username과 password를 담고 있다.
 */
public record LoginRequest(String username, String password) {
}
