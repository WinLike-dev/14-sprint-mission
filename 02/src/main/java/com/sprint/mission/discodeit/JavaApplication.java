package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.FileServiceFactory;
import com.sprint.mission.discodeit.factory.JCFServiceFactory;
import com.sprint.mission.discodeit.factory.ServiceFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class JavaApplication {

    static User setupUser(UserService userService) {
        return userService.createUser(
                "woody",
                "woody@codeit.com",
                "woody1234"
        );
    }

    static Channel setupChannel(ChannelService channelService) {
        return channelService.createChannel(
                ChannelType.PUBLIC,
                "공지",
                "공지 채널입니다."
        );
    }

    static void messageCreateTest(
            MessageService messageService,
            Channel channel,
            User author,
            User receiver
    ) {
        Message message = messageService.createMessage(
                "안녕하세요.",
                channel.getId(),
                author.getId(),
                receiver.getId()
        );
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        boolean fileMode = args.length > 0 && "file".equalsIgnoreCase(args[0]);
        ServiceFactory factory = fileMode
                ? new FileServiceFactory()
                : new JCFServiceFactory();
        UserService userService = factory.createUserService();
        ChannelService channelService = factory.createChannelService();
        MessageService messageService = factory.createMessageService();

        User author = setupUser(userService);
        User receiver = userService.createUser("receiver", "receiver@email.com", "1234");
        Channel channel = setupChannel(channelService);

        messageCreateTest(messageService, channel, author, receiver);
    }
}
