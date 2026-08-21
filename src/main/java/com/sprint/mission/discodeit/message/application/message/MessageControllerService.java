package com.sprint.mission.discodeit.message.application.message;

import com.sprint.mission.discodeit.message.application.message.dto.CreateMessageCommand;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.response.MessageDto;

import java.util.List;
import java.util.UUID;

/**
 * REST 컨트롤러가 호출하는 메시지 유스케이스 계약.
 */
public interface MessageControllerService {

    // 새 메시지를 생성한다
    MessageDto create(CreateMessageCommand command);

    // 특정 채널의 모든 메시지를 조회한다
    List<MessageDto> findAllByChannelId(UUID channelId);

    // 메시지 내용을 수정한다
    MessageDto update(UUID id, MessageUpdateRequest request);

    // 메시지를 삭제한다
    void delete(UUID id);
}
