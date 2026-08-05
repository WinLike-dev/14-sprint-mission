package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

// 자동 설정과 현재 패키지 이하의 Bean 탐색을 시작하는 Spring Boot 진입점이다.
@SpringBootApplication
public class DiscodeitApplication {

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
        // context가 Bean의 생성과 의존성 주입을 담당하므로 구현체를 직접 new하지 않는다.
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        User author = setupUser(userService);
        User receiver = userService.createUser(
                "receiver",
                "receiver@email.com",
                "1234"
        );
        Channel channel = setupChannel(channelService);

        messageCreateTest(messageService, channel, author, receiver);
    }
}
