package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.channel.adapter.out.persistence.channel.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.channel.adapter.out.persistence.readstatus.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;
import com.sprint.mission.discodeit.content.adapter.out.persistence.binarycontent.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.content.application.port.out.BinaryContentRepository;
import com.sprint.mission.discodeit.message.adapter.out.persistence.message.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.message.application.port.out.MessageRepository;
import com.sprint.mission.discodeit.user.adapter.out.persistence.status.jcf.JCFUserStatusRepository;
import com.sprint.mission.discodeit.user.adapter.out.persistence.user.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * outbound 저장소 포트에 인메모리(JCF) 어댑터를 연결하는 설정.
 *
 * discodeit.repository.type=jcf 일 때만 활성화된다. 설정 누락 시에는 활성화되지 않으며,
 * 그 경우 FileRepositoryConfig가 기본값으로 선택된다.
 * 데이터가 프로세스 종료와 함께 사라지므로 로컬 개발과 테스트를 위한 선택지다.
 */
@Configuration
@Conditional(RepositoryTypeCondition.class)
@ConditionalOnProperty(
        name = RepositoryProperties.TYPE_KEY,
        havingValue = "jcf"
)
public class JcfRepositoryConfig {

    @Bean
    public UserRepository userRepository() {
        return new JCFUserRepository();
    }

    @Bean
    public ChannelRepository channelRepository() {
        return new JCFChannelRepository();
    }

    @Bean
    public MessageRepository messageRepository() {
        return new JCFMessageRepository();
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        return new JCFReadStatusRepository();
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        return new JCFUserStatusRepository();
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return new JCFBinaryContentRepository();
    }
}
