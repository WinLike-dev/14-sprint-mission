package com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request;

/**
 * 채널 수정 요청 DTO.
 * 클라이언트가 채널 정보를 수정할 때 보내는 JSON 데이터를 담는 객체이다.
 * 각 필드가 null이면 해당 항목은 수정하지 않고 기존 값을 유지한다.
 *
 * @param name        변경할 채널 이름 (null이면 기존 값 유지)
 * @param description 변경할 채널 설명 (null이면 기존 값 유지)
 */
public record ChannelUpdateRequest(String name, String description) {
}
