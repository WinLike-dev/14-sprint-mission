package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.content.application.port.out.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;
import com.sprint.mission.discodeit.message.application.port.out.MessageRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;
import com.sprint.mission.discodeit.content.adapter.out.persistence.binarycontent.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.channel.adapter.out.persistence.channel.file.FileChannelRepository;
import com.sprint.mission.discodeit.message.adapter.out.persistence.message.file.FileMessageRepository;
import com.sprint.mission.discodeit.channel.adapter.out.persistence.readstatus.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.user.adapter.out.persistence.user.file.FileUserRepository;
import com.sprint.mission.discodeit.user.adapter.out.persistence.status.file.FileUserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

/**
 * outbound 저장소 포트에 파일 기반 어댑터를 연결하는 설정.
 * 저장 방식을 바꾸려면 이 클래스에서 구현체만 갈아끼우면 된다.
 */
@Configuration
public class RepositoryConfig {

    // 모든 파일 Repository가 데이터를 저장할 공통 루트 경로
    // application.properties의 discodeit.repository.data-root 값을 읽어오며, 기본값은 "data"
    private final Path dataRoot;

    // @Value: Spring이 설정 파일(application.properties)에서 값을 주입해준다
    public RepositoryConfig(@Value("${discodeit.repository.data-root:data}") String dataRoot) {
        this.dataRoot = Path.of(dataRoot);
    }

    // 사용자(User) 데이터를 파일로 저장하는 리포지토리 Bean 등록
    @Bean
    public UserRepository userRepository() {
        return new FileUserRepository(dataRoot);
    }

    // 채널(Channel) 데이터를 파일로 저장하는 리포지토리 Bean 등록
    @Bean
    public ChannelRepository channelRepository() {
        return new FileChannelRepository(dataRoot);
    }

    // 메시지(Message) 데이터를 파일로 저장하는 리포지토리 Bean 등록
    @Bean
    public MessageRepository messageRepository() {
        return new FileMessageRepository(dataRoot);
    }

    // 읽기 상태(ReadStatus) 데이터를 파일로 저장하는 리포지토리 Bean 등록
    @Bean
    public ReadStatusRepository readStatusRepository() {
        return new FileReadStatusRepository(dataRoot);
    }

    // 사용자 상태(UserStatus) 데이터를 파일로 저장하는 리포지토리 Bean 등록
    @Bean
    public UserStatusRepository userStatusRepository() {
        return new FileUserStatusRepository(dataRoot);
    }

    // 바이너리 콘텐츠(파일 첨부 등) 데이터를 파일로 저장하는 리포지토리 Bean 등록
    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new FileBinaryContentRepository(dataRoot);
    }
}
