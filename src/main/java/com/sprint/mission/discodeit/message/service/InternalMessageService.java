package com.sprint.mission.discodeit.message.service;

import java.util.UUID;

public interface InternalMessageService {

    void deleteAllByChannelId(UUID channelId);
}
