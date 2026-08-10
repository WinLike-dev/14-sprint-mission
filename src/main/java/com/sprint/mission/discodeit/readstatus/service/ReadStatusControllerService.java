package com.sprint.mission.discodeit.readstatus.service;

import com.sprint.mission.discodeit.readstatus.dto.response.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusControllerService {

    ReadStatusDto create(UUID userId, UUID channelId);

    ReadStatusDto find(UUID userId, UUID channelId);

    List<ReadStatusDto> findAllByUserId(UUID userId);

    ReadStatusDto updateLastReadAt(UUID userId, UUID channelId);

    void delete(UUID userId, UUID channelId);
}
