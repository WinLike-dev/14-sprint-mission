package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = Objects.requireNonNull(channelRepository);
    }

    @Override
    public Channel createChannel(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        return channelRepository.create(channel);
    }

    @Override
    public Channel loadChannel(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> loadAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public void updateChannel(UUID id, String name, String description) {
        Channel channel = channelRepository.findById(id);
        channel.update(name, description);
        channelRepository.update(channel);
    }

    @Override
    public void deleteChannel(UUID id) {
        channelRepository.deleteById(id);
    }
}
