package com.sprint.mission.discodeit.channel.domain.channel.exception;

import java.util.UUID;

/**
 * 채널에서 지원하지 않는 작업을 시도할 때 발생하는 예외.
 * 예: PRIVATE 채널은 이름/설명 수정이 불가능한데, update를 호출하면 이 예외가 발생한다.
 */
public class UnsupportedChannelOperationException extends UnsupportedOperationException {

    // channelId: 문제가 발생한 채널, operation: 시도한 작업명 (예: "update")
    public UnsupportedChannelOperationException(UUID channelId, String operation) {
        super(
                "채널에서 지원하지 않는 작업입니다. channelId=%s, operation=%s"
                        .formatted(channelId, operation)
        );
    }
}
