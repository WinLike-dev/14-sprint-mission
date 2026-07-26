package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.CrudRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JCFServiceFactory implements ServiceFactory {

    // 설계: 한 팩토리가 Repository를 소유해야 세 서비스가 같은 메모리 데이터를 공유한다.
    private final CrudRepository<User> userRepository;
    private final CrudRepository<Channel> channelRepository;
    private final CrudRepository<Message> messageRepository;

    public JCFServiceFactory() {
        this.userRepository = new JCFUserRepository();
        this.channelRepository = new JCFChannelRepository();
        this.messageRepository = new JCFMessageRepository();
    }

    @Override
    public UserService createUserService() {
        return new BasicUserService(userRepository);
    }

    @Override
    public ChannelService createChannelService() {
        return new BasicChannelService(channelRepository);
    }

    @Override
    public MessageService createMessageService() {
        return new BasicMessageService(
                messageRepository,
                userRepository,
                channelRepository
        );
    }
}
