package com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request;

/**
 * 메시지 수정 요청 DTO.
 * 클라이언트가 기존 메시지의 내용을 변경할 때 사용한다.
 */
public record MessageUpdateRequest(String content) { // content: 수정할 메시지 본문
}
