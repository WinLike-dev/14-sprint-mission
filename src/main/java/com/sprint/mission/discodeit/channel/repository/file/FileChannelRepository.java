package com.sprint.mission.discodeit.channel.repository.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;

import java.nio.file.Path;

public final class FileChannelRepository extends AbstractFileRepository<Channel>
        implements ChannelRepository {

    public FileChannelRepository(Path root) {
        super(root.resolve("channels"), Channel.class);
    }
}
