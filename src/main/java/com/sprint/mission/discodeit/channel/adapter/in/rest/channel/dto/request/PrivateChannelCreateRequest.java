package com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 비공개(DM) 채널 생성 요청 DTO.
 * 클라이언트가 비공개 채널을 만들 때 참여자 ID 목록을 보내는 객체이다.
 *
 * @param participantIds 채널에 참여할 사용자 ID 목록
 */
public record PrivateChannelCreateRequest(List<UUID> participantIds) {

    // compact constructor: record 생성 시 자동 호출되어, 참여자 목록을 불변 리스트로 복사한다
    // 외부에서 원본 리스트를 변경해도 이 객체에 영향을 주지 않도록 방어적 복사를 수행
    public PrivateChannelCreateRequest {
        participantIds = List.copyOf(Objects.requireNonNull(participantIds));
    }
}
