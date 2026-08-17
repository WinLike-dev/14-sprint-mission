package com.sprint.mission.discodeit.message.adapter.out.integration.content;

import com.sprint.mission.discodeit.content.api.BinaryContentPayload;
import com.sprint.mission.discodeit.content.api.ContentInternalApi;
import com.sprint.mission.discodeit.message.application.port.out.MessageContentData;
import com.sprint.mission.discodeit.message.application.port.out.MessageContentManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * MessageContentManager outbound 포트의 ACL 어댑터.
 * MessageContentData를 content 모듈의 BinaryContentPayload로 바꿔 노출 API를 호출한다.
 */
@Component
@RequiredArgsConstructor
public class MessageContentAclAdapter implements MessageContentManager {

    private final ContentInternalApi contentInternalApi; // Content 모듈이 제공하는 내부 API

    // 메시지 모듈의 데이터를 Content 모듈의 형식으로 변환하여 첨부파일을 생성한다
    @Override
    public UUID create(MessageContentData request) {
        return contentInternalApi.create(new BinaryContentPayload(
                request.fileName(), request.contentType(), request.bytes()
        ));
    }

    // Content 모듈의 내부 API를 호출하여 첨부파일을 삭제한다
    @Override
    public void delete(UUID contentId) {
        contentInternalApi.delete(contentId);
    }
}
