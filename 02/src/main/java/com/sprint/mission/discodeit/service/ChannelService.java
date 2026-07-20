package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel createChannel(ChannelType type, String name, String description);

    Channel readChannel(UUID id);

    List<Channel> readAllChannels();

    void updateChannel(UUID id, String name, String description);

    void deleteChannel(UUID id);
}
