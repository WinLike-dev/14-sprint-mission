package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.CrudRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageRepository implements CrudRepository<Message> {

    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        data = new HashMap<>();
    }

    @Override
    public Message create(Message message) {
        if (data.containsKey(message.getId())) {
            throw new IllegalStateException(
                    "이미 존재하는 메시지입니다: " + message.getId()
            );
        }

        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalStateException(
                    "메시지 데이터를 찾을 수 없습니다: " + id
            );
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(Message message) {
        findById(message.getId());
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public void deleteById(UUID id) {
        findById(id);
        data.remove(id);
    }
}
