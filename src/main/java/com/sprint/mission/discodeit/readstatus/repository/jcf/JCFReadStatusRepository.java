package com.sprint.mission.discodeit.readstatus.repository.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class JCFReadStatusRepository extends AbstractJCFRepository<ReadStatus>
        implements ReadStatusRepository {

    public JCFReadStatusRepository() {
        super(ReadStatus.class, ReadStatus::copy);
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
