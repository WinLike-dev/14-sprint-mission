package com.sprint.mission.discodeit.content.adapter.out.persistence.binarycontent.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.common.repository.file.FileLockProvider;
import com.sprint.mission.discodeit.content.domain.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.content.application.port.out.BinaryContentRepository;

import java.nio.file.Path;

// 파생 조회는 BinaryContentRepository의 default 구현을 그대로 쓴다.
public final class FileBinaryContentRepository extends AbstractFileRepository<BinaryContent>
        implements BinaryContentRepository {

    public FileBinaryContentRepository(Path root, FileLockProvider lockProvider) {
        super(root.resolve("binary-contents"), BinaryContent.class, lockProvider);
    }
}
