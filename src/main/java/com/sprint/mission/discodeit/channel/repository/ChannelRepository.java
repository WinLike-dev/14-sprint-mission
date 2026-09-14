package com.sprint.mission.discodeit.channel.repository;

import com.sprint.mission.discodeit.channel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 채널 저장소 outbound 포트.
 * 구현은 Spring Data JPA가 런타임에 만든다.
 */
public interface ChannelRepository extends JpaRepository<Channel, UUID> {
}
