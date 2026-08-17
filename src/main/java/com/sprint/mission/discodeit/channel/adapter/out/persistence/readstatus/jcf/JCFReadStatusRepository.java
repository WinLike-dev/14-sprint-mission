package com.sprint.mission.discodeit.channel.adapter.out.persistence.readstatus.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;

// 파생 조회는 ReadStatusRepository의 default 구현을 그대로 쓴다.
public final class JCFReadStatusRepository extends AbstractJCFRepository<ReadStatus>
        implements ReadStatusRepository {

    public JCFReadStatusRepository() {
        super(ReadStatus.class, ReadStatus::copy);
    }
}
