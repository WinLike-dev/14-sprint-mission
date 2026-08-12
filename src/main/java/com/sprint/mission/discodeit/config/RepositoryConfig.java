package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import com.sprint.mission.discodeit.binarycontent.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.channel.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.message.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.readstatus.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.userstatus.repository.file.FileUserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class RepositoryConfig {

    // 모든 파일 Repository가 데이터를 저장할 공통 루트 경로
    private final Path dataRoot;

    public RepositoryConfig(@Value("${discodeit.repository.data-root:data}") String dataRoot) {
        this.dataRoot = Path.of(dataRoot);
    }


    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository(dataRoot);
    }

    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository(dataRoot);
    }

    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository(dataRoot);
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        return new FileReadStatusRepository(dataRoot);
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        return new FileUserStatusRepository(dataRoot);
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new FileBinaryContentRepository(dataRoot);
    }
}
