package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Message {

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String content;
    private final UUID channelId;
    private final UUID senderId;
    private final UUID receiverId;

    public Message(String content, UUID channelId, UUID senderId, UUID receiverId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;
        this.content = content;
        this.channelId = channelId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

}
