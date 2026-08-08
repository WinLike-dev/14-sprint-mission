package com.sprint.mission.discodeit.binarycontent.repository.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;

public final class JCFBinaryContentRepository extends AbstractJCFRepository<BinaryContent>
        implements BinaryContentRepository {

    public JCFBinaryContentRepository() {
        super(BinaryContent.class, BinaryContent::copy);
    }
}
