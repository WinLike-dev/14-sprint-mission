package com.sprint.mission.discodeit.channel.adapter.out.persistence.readstatus.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;

import java.nio.file.Path;

// 파생 조회는 ReadStatusRepository의 default 구현을 그대로 쓴다.
public final class FileReadStatusRepository extends AbstractFileRepository<ReadStatus>
        implements ReadStatusRepository {

    public FileReadStatusRepository(Path root) {
        super(root.resolve("read-statuses"), ReadStatus.class);
    }
}
