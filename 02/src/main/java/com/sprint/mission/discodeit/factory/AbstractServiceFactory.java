package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.objectStore.ObjectStore;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public abstract class AbstractServiceFactory implements ServiceFactory {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    protected AbstractServiceFactory(
            ObjectStore<User> userStore,
            ObjectStore<Channel> channelStore,
            ObjectStore<Message> messageStore
    ) {
        this.userRepository = new UserRepository(userStore);
        this.channelRepository = new ChannelRepository(channelStore);
        this.messageRepository = new MessageRepository(messageStore);
    }

    @Override
    public final UserService createUserService() {
        return new BasicUserService(userRepository);
    }

    @Override
    public final ChannelService createChannelService() {
        return new BasicChannelService(channelRepository);
    }

    @Override
    public final MessageService createMessageService() {
        return new BasicMessageService(
                messageRepository,
                userRepository,
                channelRepository
        );
    }
}
