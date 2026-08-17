package com.sprint.mission.discodeit.content.application.internal;

import com.sprint.mission.discodeit.content.domain.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.content.application.port.out.BinaryContentRepository;
import com.sprint.mission.discodeit.content.api.BinaryContentPayload;
import com.sprint.mission.discodeit.content.api.ContentInternalApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * ContentInternalApi의 구현체.
 * REST가 아니라 다른 모듈의 outbound 어댑터가 생성/삭제할 때 사용한다.
 */
@Service
@RequiredArgsConstructor
public class ContentInternalService implements ContentInternalApi {

    private final BinaryContentRepository binaryContentRepository;

    // 바이너리 콘텐츠를 생성하고 생성된 ID를 반환한다
    @Override
    public UUID create(BinaryContentPayload payload) {
        BinaryContentPayload target = Objects.requireNonNull(payload);
        BinaryContent content = new BinaryContent(
                target.fileName(), target.contentType(), target.bytes()
        );
        return binaryContentRepository.create(content).getId(); // 저장 후 생성된 ID를 반환
    }

    // ID로 바이너리 콘텐츠를 삭제한다
    @Override
    public void delete(UUID contentId) {
        binaryContentRepository.deleteById(contentId);
    }
}
