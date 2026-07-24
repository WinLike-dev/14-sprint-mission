package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.CrudRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import lombok.Getter;

@Getter
public class ServiceFactory {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    private ServiceFactory() {
        this(
                new JCFUserRepository(),
                new JCFChannelRepository(),
                new JCFMessageRepository()
        );
    }

    private ServiceFactory(
            CrudRepository<User> userRepository,
            CrudRepository<Channel> channelRepository,
            CrudRepository<Message> messageRepository
    ) {
        // 설계: Factory만 구체 Repository를 알고 서비스에는 제네릭 계약을 주입한다.
        userService = new BasicUserService(userRepository);
        channelService = new BasicChannelService(channelRepository);
        messageService = new BasicMessageService(
                messageRepository,
                userRepository,
                channelRepository
        );
    }

    private static class LazyHolder {
        private static final ServiceFactory INSTANCE = new ServiceFactory();
    }

    public static ServiceFactory getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static ServiceFactory createFileFactory() {
        return new ServiceFactory(
                new FileUserRepository(),
                new FileChannelRepository(),
                new FileMessageRepository()
        );
    }
}
