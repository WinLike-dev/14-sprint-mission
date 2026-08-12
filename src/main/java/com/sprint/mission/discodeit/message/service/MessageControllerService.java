package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.dto.response.MessageDto;

import java.util.List;
import java.util.UUID;

public interface MessageControllerService {

    MessageDto create(MessageCreateRequest request);

    List<MessageDto> findAllByChannelId(UUID channelId);

    MessageDto update(UUID id, MessageUpdateRequest request);

    void delete(UUID id);
}
