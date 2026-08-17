package com.sprint.mission.discodeit.channel.domain.readstatus.exception;

import com.sprint.mission.discodeit.common.exception.ConflictingStateException;

import java.util.UUID;

/**
 * 비공개 채널에 수신 정보를 따로 만들려고 할 때 발생한다.
 *
 * 비공개 채널은 생성 시점에 참여자별 수신 정보가 함께 만들어지므로 추가 생성이 막힌다.
 * 요청 형식의 문제가 아니라 그 채널이 PRIVATE이라는 상태 때문에 거부된다.
 */
public class ReadStatusCreationNotAllowedException extends ConflictingStateException {

    public ReadStatusCreationNotAllowedException(UUID channelId) {
        super("비공개 채널의 수신 정보는 채널 생성 과정에서만 만들 수 있습니다: channelId=" + channelId);
    }
}
