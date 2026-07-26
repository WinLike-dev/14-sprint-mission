package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

// 패턴: 추상 팩토리는 구체 서비스의 생성 과정과 Repository 연결을 호출 코드에서 숨긴다.
public interface ServiceFactory {

    UserService createUserService();

    ChannelService createChannelService();

    MessageService createMessageService();
}
