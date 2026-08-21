package com.sprint.mission.discodeit.channel.adapter.out.persistence.channel.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.common.repository.file.FileLockProvider;
import com.sprint.mission.discodeit.channel.domain.channel.Channel;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;

import java.nio.file.Path;

public final class FileChannelRepository extends AbstractFileRepository<Channel>
        implements ChannelRepository {

    public FileChannelRepository(Path root, FileLockProvider lockProvider) {
        super(root.resolve("channels"), Channel.class, lockProvider);
    }
}
