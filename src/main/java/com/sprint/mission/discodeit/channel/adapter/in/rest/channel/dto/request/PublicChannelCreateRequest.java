package com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request;

/**
 * 공개 채널 생성 요청 DTO.
 * 클라이언트가 공개 채널을 만들 때 보내는 JSON 데이터를 담는 객체이다.
 * record를 사용하면 불변 객체가 자동으로 만들어진다 (getter, equals, hashCode, toString 자동 생성).
 *
 * @param name        채널 이름 (필수)
 * @param description 채널 설명 (필수)
 */
public record PublicChannelCreateRequest(String name, String description) {
}
