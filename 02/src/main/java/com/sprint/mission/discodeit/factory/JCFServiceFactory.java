package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import lombok.Getter;

@Getter
public class JCFServiceFactory implements ServiceFactory {

    // 기본 서비스 팩토리는 JCF로 구성
    private JCFServiceFactory() {
    }

    public BasicUserService createUserService() {
        return new BasicUserService(new JCFUserRepository());
    }

    public BasicMessageService createMessageService() {
        return new BasicMessageService(
                new JCFMessageRepository(),
                new JCFUserRepository(),
                new JCFChannelRepository());
    }

    public BasicChannelService createChannelService() {
        return new BasicChannelService(new JCFChannelRepository());
    }
}
