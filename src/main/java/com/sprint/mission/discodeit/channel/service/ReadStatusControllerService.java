package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.service.dto.command.CreateReadStatusCommand;
import com.sprint.mission.discodeit.channel.service.dto.result.ReadStatusResult;
import com.sprint.mission.discodeit.channel.service.dto.command.UpdateReadStatusCommand;

import java.util.List;
import java.util.UUID;

/**
 * 읽음 상태(ReadStatus) 관련 비즈니스 로직의 인터페이스.
 * 컨트롤러(ReadStatusController)가 이 인터페이스에 의존하여 구현체와 분리된다.
 */
public interface ReadStatusControllerService {

    // 특정 사용자의 특정 채널에 대한 읽음 상태 생성
    ReadStatusResult create(CreateReadStatusCommand command);

    // 특정 사용자 + 특정 채널의 읽음 상태 단건 조회
    ReadStatusResult find(UUID userId, UUID channelId);

    // 특정 사용자의 모든 읽음 상태 조회
    List<ReadStatusResult> findAllByUserId(UUID userId);

    // 읽음 시각을 현재 시각으로 갱신 (사용자가 채널을 열었을 때 호출)
    ReadStatusResult updateLastReadAt(UUID readStatusId, UpdateReadStatusCommand command);

    // 읽음 상태 삭제
    void delete(UUID userId, UUID channelId);
}
