package com.sprint.mission.discodeit.message.application.message;

import com.sprint.mission.discodeit.message.application.port.out.MessageContentManager;
import com.sprint.mission.discodeit.message.domain.message.Message;
import com.sprint.mission.discodeit.message.application.port.out.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 메시지 일괄 정리 유스케이스.
 * inbound 이벤트 핸들러(ChannelDeletedEventHandler)가 호출한다.
 */
@Service
@RequiredArgsConstructor
public class MessageCleanupService {

    private final MessageRepository messageRepository;
    private final MessageContentManager contentManager;

    // 특정 채널의 모든 메시지를 찾아서 하나씩 삭제한다
    public void deleteAllByChannelId(UUID channelId) {
        messageRepository.findAllByChannelId(channelId).forEach(this::deleteMessage);
    }

    // 메시지에 달린 첨부파일을 먼저 삭제하고, 그다음 메시지 자체를 삭제한다
    private void deleteMessage(Message message) {
        message.getAttachmentIds().forEach(contentManager::delete);
        messageRepository.deleteById(message.getId());
    }
}
