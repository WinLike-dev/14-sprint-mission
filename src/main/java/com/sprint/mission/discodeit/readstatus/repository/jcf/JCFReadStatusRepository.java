package com.sprint.mission.discodeit.readstatus.repository.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;

public final class JCFReadStatusRepository extends AbstractJCFRepository<ReadStatus>
        implements ReadStatusRepository {

    public JCFReadStatusRepository() {
        super(ReadStatus.class, ReadStatus::copy);
    }
}
