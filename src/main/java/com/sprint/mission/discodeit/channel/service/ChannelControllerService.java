package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.response.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelControllerService {

    ChannelDto createPublic(PublicChannelCreateRequest request);

    ChannelDto createPrivate(PrivateChannelCreateRequest request);

    ChannelDto find(UUID id);

    List<ChannelDto> findAllByUserId(UUID userId);

    ChannelDto update(UUID id, ChannelUpdateRequest request);

    void delete(UUID id);
}
