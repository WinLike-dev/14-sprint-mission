package com.sprint.mission.discodeit.channel.application.channel;

import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.response.ChannelDto;

import java.util.List;
import java.util.UUID;

/**
 * REST 컨트롤러가 호출하는 채널 유스케이스 계약.
 * 구현은 ChannelServiceImpl이 담당한다.
 */
public interface ChannelControllerService {

    // 공개 채널 생성
    ChannelDto createPublic(PublicChannelCreateRequest request);

    // 비공개(DM) 채널 생성
    ChannelDto createPrivate(PrivateChannelCreateRequest request);

    // ID로 채널 단건 조회
    ChannelDto find(UUID id);

    // 특정 사용자가 접근 가능한 모든 채널 조회 (공개 채널 + 참여 중인 비공개 채널)
    List<ChannelDto> findAllByUserId(UUID userId);

    // 채널 정보(이름, 설명) 수정
    ChannelDto update(UUID id, ChannelUpdateRequest request);

    // 채널 삭제
    void delete(UUID id);
}
