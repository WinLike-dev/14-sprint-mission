package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DiscodeitApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoadsRepositoryAndServiceBeans() {
        assertNotNull(context.getBean(UserRepository.class));
        assertNotNull(context.getBean(ChannelRepository.class));
        assertNotNull(context.getBean(MessageRepository.class));
        assertNotNull(context.getBean(UserService.class));
        assertNotNull(context.getBean(ChannelService.class));
        assertNotNull(context.getBean(MessageService.class));
    }
}
