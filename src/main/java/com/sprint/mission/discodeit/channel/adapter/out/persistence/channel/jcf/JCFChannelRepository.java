package com.sprint.mission.discodeit.channel.adapter.out.persistence.channel.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.channel.domain.channel.Channel;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;

public final class JCFChannelRepository extends AbstractJCFRepository<Channel>
        implements ChannelRepository {

    public JCFChannelRepository() {
        super(Channel.class, Channel::copy);
    }
}
