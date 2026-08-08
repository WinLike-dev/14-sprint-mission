package com.sprint.mission.discodeit.channel.exception;

import java.util.UUID;

public class UnsupportedChannelOperationException extends UnsupportedOperationException {

    public UnsupportedChannelOperationException(UUID channelId, String operation) {
        super(
                "채널에서 지원하지 않는 작업입니다. channelId=%s, operation=%s"
                        .formatted(channelId, operation)
        );
    }
}
