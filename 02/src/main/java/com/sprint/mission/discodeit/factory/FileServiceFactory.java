package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.CrudRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class FileServiceFactory implements ServiceFactory {

    // 설계: 파일 모드도 하나의 팩토리 안에서 Repository 생명주기를 함께 관리한다.
    private final CrudRepository<User> userRepository;
    private final CrudRepository<Channel> channelRepository;
    private final CrudRepository<Message> messageRepository;

    public FileServiceFactory() {
        this.userRepository = new FileUserRepository();
        this.channelRepository = new FileChannelRepository();
        this.messageRepository = new FileMessageRepository();
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
