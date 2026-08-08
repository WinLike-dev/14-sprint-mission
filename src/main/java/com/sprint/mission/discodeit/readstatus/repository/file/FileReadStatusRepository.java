package com.sprint.mission.discodeit.readstatus.repository.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;

import java.nio.file.Path;

public final class FileReadStatusRepository extends AbstractFileRepository<ReadStatus>
        implements ReadStatusRepository {

    public FileReadStatusRepository(Path root) {
        super(root.resolve("read-statuses"), ReadStatus.class);
    }
}
