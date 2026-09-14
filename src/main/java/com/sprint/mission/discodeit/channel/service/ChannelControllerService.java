package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.service.dto.ChannelResult;
import com.sprint.mission.discodeit.channel.service.dto.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.UpdatePublicChannelCommand;

import java.util.List;
import java.util.UUID;

/**
 * REST 컨트롤러가 호출하는 채널 유스케이스 계약.
 * 구현은 ChannelServiceImpl이 담당한다.
 */
public interface ChannelControllerService {

    // 공개 채널 생성
    ChannelResult createPublic(CreatePublicChannelCommand command);

    // 비공개(DM) 채널 생성
    ChannelResult createPrivate(CreatePrivateChannelCommand command);

    // ID로 채널 단건 조회
    ChannelResult find(UUID id);

    // 특정 사용자가 접근 가능한 모든 채널 조회 (공개 채널 + 참여 중인 비공개 채널)
    List<ChannelResult> findAllByUserId(UUID userId);

    // 채널 정보(이름, 설명) 수정
    ChannelResult update(UUID id, UpdatePublicChannelCommand command);

    // 채널 삭제
    void delete(UUID id);
}
