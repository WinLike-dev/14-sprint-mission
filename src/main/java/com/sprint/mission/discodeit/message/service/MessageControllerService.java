package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.message.service.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.message.service.dto.result.MessageResult;
import com.sprint.mission.discodeit.message.service.dto.command.UpdateMessageCommand;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.UUID;

/**
 * REST 컨트롤러가 호출하는 메시지 유스케이스 계약.
 */
public interface MessageControllerService {

    // 새 메시지를 생성한다
    MessageResult create(CreateMessageCommand command);

    // 특정 채널의 메시지를 페이지 단위로 조회한다
    Slice<MessageResult> findAllByChannelId(UUID channelId, Pageable pageable);

    // 메시지 내용을 수정한다
    MessageResult update(UUID id, UpdateMessageCommand command);

    // 메시지를 삭제한다
    void delete(UUID id);
}
