package com.sprint.mission.discodeit.content.adapter.out.persistence.binarycontent.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.content.domain.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.content.application.port.out.BinaryContentRepository;

import java.util.List;
import java.util.UUID;

public final class JCFBinaryContentRepository extends AbstractJCFRepository<BinaryContent>
        implements BinaryContentRepository {

    public JCFBinaryContentRepository() {
        super(BinaryContent.class, BinaryContent::copy);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream().map(this::getById).toList();
    }
}
