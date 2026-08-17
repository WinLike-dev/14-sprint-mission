package com.sprint.mission.discodeit.channel.domain.readstatus.exception;

import java.util.UUID;

/**
 * PRIVATE 채널에 대해 읽음 상태를 별도로 생성하려고 할 때 발생하는 예외.
 * PRIVATE 채널의 읽음 상태는 채널 생성 시점에 참여자와 함께 자동으로 만들어지기 때문에,
 * 이후에 따로 생성하는 것은 허용되지 않는다.
 */
public class ReadStatusCreationNotAllowedException extends RuntimeException {

    public ReadStatusCreationNotAllowedException(UUID channelId) {
        super("비공개 채널의 수신 정보는 채널 생성 과정에서만 만들 수 있습니다: channelId=" + channelId);
    }
}
