package com.sprint.mission.discodeit.channel.domain.channel.exception;

import com.sprint.mission.discodeit.common.exception.ConflictingStateException;

import java.util.UUID;

/**
 * 채널의 현재 타입에서 허용되지 않는 작업을 시도했을 때 발생한다.
 *
 * 예전에는 UnsupportedOperationException을 상속했다. 그건 "이 구현체는 이 연산을
 * 제공하지 않는다"는 JDK의 관용구지, 도메인 규칙 위반을 뜻하지 않는다.
 * 같은 update라도 PUBLIC 채널에서는 성공하므로 구현의 부재가 아니라 상태의 문제다.
 */
public class UnsupportedChannelOperationException extends ConflictingStateException {

    public UnsupportedChannelOperationException(UUID channelId, String operation) {
        super(
                "채널에서 지원하지 않는 작업입니다. channelId=%s, operation=%s"
                        .formatted(channelId, operation)
        );
    }
}
