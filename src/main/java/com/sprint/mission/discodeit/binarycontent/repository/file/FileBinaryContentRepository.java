package com.sprint.mission.discodeit.binarycontent.repository.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;

import java.nio.file.Path;

public final class FileBinaryContentRepository extends AbstractFileRepository<BinaryContent>
        implements BinaryContentRepository {

    public FileBinaryContentRepository(Path root) {
        super(root.resolve("binary-contents"), BinaryContent.class);
    }
}
