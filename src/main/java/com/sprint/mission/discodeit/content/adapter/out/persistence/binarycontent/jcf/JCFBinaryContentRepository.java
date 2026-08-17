package com.sprint.mission.discodeit.content.adapter.out.persistence.binarycontent.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.content.domain.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.content.application.port.out.BinaryContentRepository;

// 파생 조회는 BinaryContentRepository의 default 구현을 그대로 쓴다.
public final class JCFBinaryContentRepository extends AbstractJCFRepository<BinaryContent>
        implements BinaryContentRepository {

    public JCFBinaryContentRepository() {
        super(BinaryContent.class, BinaryContent::copy);
    }
}
