package com.sprint.mission.discodeit.user.adapter.out.integration.content;

import com.sprint.mission.discodeit.content.api.BinaryContentPayload;
import com.sprint.mission.discodeit.content.api.ContentInternalApi;
import com.sprint.mission.discodeit.user.application.port.out.UserContentData;
import com.sprint.mission.discodeit.user.application.port.out.UserContentManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * UserContentManager outbound 포트의 어댑터.
 * UserContentData를 content 모듈의 BinaryContentPayload로 바꿔 노출 API를 호출한다.
 */
@Component
@RequiredArgsConstructor
public class UserContentAcl implements UserContentManager {

    private final ContentInternalApi contentInternalApi; // content 모듈이 제공하는 내부 API

    // user 모듈의 데이터를 content 모듈 형식으로 변환하여 콘텐츠를 생성한다.
    @Override
    public UUID create(UserContentData request) {
        return contentInternalApi.create(new BinaryContentPayload(
                request.fileName(), request.contentType(), request.bytes()
        ));
    }

    // 해당 ID의 콘텐츠를 content 모듈에 삭제 요청한다.
    @Override
    public void delete(UUID contentId) {
        contentInternalApi.delete(contentId);
    }
}
