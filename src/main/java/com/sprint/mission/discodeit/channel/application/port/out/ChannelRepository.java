package com.sprint.mission.discodeit.channel.application.port.out;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.channel.domain.channel.Channel;

/**
 * 채널 저장소 outbound 포트.
 * 파일/JCF 구현은 adapter.out.persistence가 담당한다.
 */
public interface ChannelRepository extends CrudRepository<Channel> {
}
