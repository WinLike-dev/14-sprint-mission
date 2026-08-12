package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.channel.dto.response.ChannelDto;
import com.sprint.mission.discodeit.message.dto.response.MessageDto;
import com.sprint.mission.discodeit.user.dto.response.UserDto;
import com.sprint.mission.discodeit.channel.service.ChannelControllerService;
import com.sprint.mission.discodeit.message.service.MessageControllerService;
import com.sprint.mission.discodeit.user.service.UserControllerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.UUID;

// Spring Boot가 현재 패키지 아래의 Configuration과 Service Bean을 탐색하는 진입점이다.
@SpringBootApplication
public class DiscodeitApplication {

    // 문법: static 메서드는 애플리케이션 객체를 만들지 않고 main에서 바로 호출할 수 있다.
    static UserDto setupUser(UserControllerService userControllerService) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return userControllerService.create(
                new UserCreateRequest(
                        "woody-" + suffix,
                        "woody-" + suffix + "@codeit.com",
                        "woody1234"
                ),
                null
        );
    }

    static ChannelDto setupChannel(ChannelControllerService channelControllerService) {
        return channelControllerService.createPublic(
                new PublicChannelCreateRequest("공지", "공지 채널입니다.")
        );
    }

    static void messageCreateTest(
            MessageControllerService messageControllerService,
            ChannelDto channel,
            UserDto author
    ) {
        MessageDto message = messageControllerService.create(
                new MessageCreateRequest(
                        "안녕하세요.",
                        channel.id(),
                        author.id(),
                        List.of()
                )
        );
        System.out.println("메시지 생성: " + message.id());
    }

    public static void main(String[] args) {
        // IoC Container가 Bean 생성과 의존성 주입을 마친 뒤 인터페이스 타입으로 Bean을 조회한다.
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        UserControllerService userControllerService =
                context.getBean(UserControllerService.class);
        ChannelControllerService channelControllerService =
                context.getBean(ChannelControllerService.class);
        MessageControllerService messageControllerService =
                context.getBean(MessageControllerService.class);

        UserDto user = setupUser(userControllerService);
        ChannelDto channel = setupChannel(channelControllerService);
        messageCreateTest(messageControllerService, channel, user);
    }
}
