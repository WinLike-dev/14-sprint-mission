package com.sprint.mission.discodeit.channel.repository;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 채널 저장소.
 * 구현은 Spring Data JPA가 런타임에 만든다.
 */
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    // 특정 유형의 채널만 조회한다. 참여 중인 PRIVATE 채널이 없을 때 쓴다.
    List<Channel> findAllByType(ChannelType type);

    // 사용자가 볼 수 있는 채널을 한 번에 조회한다.
    // 전체를 읽어 메모리에서 거르면 채널이 늘어날수록 읽는 양이 같이 늘어난다.
    List<Channel> findAllByTypeOrIdIn(ChannelType type, Collection<UUID> ids);
}
