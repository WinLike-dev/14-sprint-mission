package com.sprint.mission.discodeit.channel.adapter.out.persistence.readstatus.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class FileReadStatusRepository extends AbstractFileRepository<ReadStatus>
        implements ReadStatusRepository {

    public FileReadStatusRepository(Path root) {
        super(root.resolve("read-statuses"), ReadStatus.class);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(status -> status.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .filter(status -> status.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        findAllByUserId(userId).forEach(status -> deleteById(status.getId()));
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId).forEach(status -> deleteById(status.getId()));
    }
}
