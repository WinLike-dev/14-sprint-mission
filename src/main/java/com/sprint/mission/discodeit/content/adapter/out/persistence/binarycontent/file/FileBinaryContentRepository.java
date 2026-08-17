package com.sprint.mission.discodeit.content.adapter.out.persistence.binarycontent.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.content.domain.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.content.application.port.out.BinaryContentRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public final class FileBinaryContentRepository extends AbstractFileRepository<BinaryContent>
        implements BinaryContentRepository {

    public FileBinaryContentRepository(Path root) {
        super(root.resolve("binary-contents"), BinaryContent.class);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream().map(this::getById).toList();
    }
}
