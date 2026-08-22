package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.channel.adapter.out.persistence.channel.file.FileChannelRepository;
import com.sprint.mission.discodeit.channel.adapter.out.persistence.readstatus.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;
import com.sprint.mission.discodeit.content.adapter.out.persistence.binarycontent.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.content.application.port.out.BinaryContentRepository;
import com.sprint.mission.discodeit.common.repository.file.FileLockProvider;
import com.sprint.mission.discodeit.message.adapter.out.persistence.message.file.FileMessageRepository;
import com.sprint.mission.discodeit.message.application.port.out.MessageRepository;
import com.sprint.mission.discodeit.user.adapter.out.persistence.status.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.user.adapter.out.persistence.user.file.FileUserRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

/**
 * outbound 저장소 포트에 파일 기반 어댑터를 연결하는 설정.
 *
 * discodeit.repository.type=file 일 때 활성화되며, 설정이 누락된 경우에도 활성화된다
 * (matchIfMissing = true). 데이터가 사라지는 인메모리보다 영속 저장소를 기본값으로 두는 정책이다.
 * JcfRepositoryConfig와 동시에 활성화되지 않으므로 포트 하나당 Bean도 하나만 등록된다.
 * 저장 기술을 바꾸는 데 Java 코드 수정이 필요하지 않다는 점이 이 구조의 목적이다.
 */
@Configuration
@Conditional(RepositoryTypeCondition.class)
@ConditionalOnProperty(
        name = RepositoryProperties.TYPE_KEY,
        havingValue = "file",
        matchIfMissing = true
)
public class FileRepositoryConfig {

    // 모든 파일 어댑터가 공유하는 데이터 루트 경로
    private final Path dataRoot;

    // 파일 하나당 잠금 하나. 저장소들이 같은 인스턴스를 공유해야
    // 같은 파일에 대한 잠금이 하나로 모인다.
    private final FileLockProvider lockProvider = new FileLockProvider();

    // RepositoryProperties를 주입받아 경로 설정까지 한곳에서 검증된 값으로 다룬다.
    public FileRepositoryConfig(RepositoryProperties properties) {
        this.dataRoot = properties.dataRootPath();
    }

    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository(dataRoot, lockProvider);
    }

    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository(dataRoot, lockProvider);
    }

    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository(dataRoot, lockProvider);
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        return new FileReadStatusRepository(dataRoot, lockProvider);
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        return new FileUserStatusRepository(dataRoot, lockProvider);
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new FileBinaryContentRepository(dataRoot, lockProvider);
    }
}
