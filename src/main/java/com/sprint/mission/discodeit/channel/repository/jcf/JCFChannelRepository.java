package com.sprint.mission.discodeit.channel.repository.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;

public final class JCFChannelRepository extends AbstractJCFRepository<Channel>
        implements ChannelRepository {

    public JCFChannelRepository() {
        super(Channel.class, Channel::copy);
    }
}
