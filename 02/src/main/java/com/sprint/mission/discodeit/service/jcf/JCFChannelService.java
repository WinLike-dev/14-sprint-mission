package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channelMap;

    public JCFChannelService() {
        channelMap = new HashMap<>();
    }

    @Override
    public Channel createChannel(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        channelMap.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel readChannel(UUID id) {
        Channel channel = channelMap.get(id);

        if (channel == null) {
            throw new NoSuchElementException("채널을 찾을 수 없습니다: " + id);
        }
        return channel;
    }

    @Override
    public List<Channel> readAllChannels() {
        return channelMap.values().stream()
                .toList();
    }

    @Override
    public void updateChannel(UUID id, String name, String description) {
        Channel channel = readChannel(id);
        channel.update(name, description);
    }

    @Override
    public void deleteChannel(UUID id) {
        readChannel(id);
        channelMap.remove(id);
    }
}
