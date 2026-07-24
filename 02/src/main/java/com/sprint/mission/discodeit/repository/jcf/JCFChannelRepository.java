package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.CrudRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelRepository implements CrudRepository<Channel> {

    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        data = new HashMap<>();
    }

    @Override
    public Channel create(Channel channel) {
        if (data.containsKey(channel.getId())) {
            throw new IllegalStateException(
                    "이미 존재하는 채널입니다: " + channel.getId()
            );
        }

        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalStateException(
                    "채널 데이터를 찾을 수 없습니다: " + id
            );
        }

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(Channel channel) {
        findById(channel.getId());
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        data.remove(id);
    }
}
