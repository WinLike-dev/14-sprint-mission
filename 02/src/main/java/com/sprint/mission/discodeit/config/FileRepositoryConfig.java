package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.objectStore.FileObjectStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

// 객체 생성과 의존성 연결을 Spring IoC Container에 알려주는 설정 객체다.
@Configuration
public class FileRepositoryConfig {

    // @Bean 메서드의 반환 객체는 Spring이 보관하고 필요한 Service에 주입한다.
    @Bean
    public UserRepository userRepository() {
        return new UserRepository(
                new FileObjectStore<>(Path.of("data", "users"), User.class)
        );
    }

    @Bean
    public ChannelRepository channelRepository() {
        return new ChannelRepository(
                new FileObjectStore<>(Path.of("data", "channels"), Channel.class)
        );
    }

    @Bean
    public MessageRepository messageRepository() {
        return new MessageRepository(
                new FileObjectStore<>(Path.of("data", "messages"), Message.class)
        );
    }
}
