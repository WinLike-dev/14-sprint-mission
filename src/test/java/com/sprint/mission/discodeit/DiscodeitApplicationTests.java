package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
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
import com.sprint.mission.discodeit.auth.service.AuthControllerService;
import com.sprint.mission.discodeit.auth.service.AuthServiceImpl;
import com.sprint.mission.discodeit.binarycontent.service.InternalBinaryContentServiceImpl;
import com.sprint.mission.discodeit.channel.service.ChannelControllerService;
import com.sprint.mission.discodeit.channel.service.ChannelServiceImpl;
import com.sprint.mission.discodeit.message.service.MessageControllerService;
import com.sprint.mission.discodeit.message.service.MessageServiceImpl;
import com.sprint.mission.discodeit.readstatus.service.ReadStatusControllerService;
import com.sprint.mission.discodeit.readstatus.service.ReadStatusServiceImpl;
import com.sprint.mission.discodeit.user.service.UserControllerService;
import com.sprint.mission.discodeit.user.service.UserServiceImpl;
import com.sprint.mission.discodeit.userstatus.service.UserStatusControllerService;
import com.sprint.mission.discodeit.userstatus.service.UserStatusServiceImpl;
import com.sprint.mission.discodeit.binarycontent.service.InternalBinaryContentService;
import com.sprint.mission.discodeit.message.service.InternalMessageService;
import com.sprint.mission.discodeit.userstatus.service.InternalUserStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
class DiscodeitApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoadsRepositoryAndServiceBeans() {
        assertInstanceOf(FileUserRepository.class, context.getBean(UserRepository.class));
        assertInstanceOf(FileChannelRepository.class, context.getBean(ChannelRepository.class));
        assertInstanceOf(FileMessageRepository.class, context.getBean(MessageRepository.class));
        assertInstanceOf(FileReadStatusRepository.class, context.getBean(ReadStatusRepository.class));
        assertInstanceOf(FileUserStatusRepository.class, context.getBean(UserStatusRepository.class));
        assertInstanceOf(
                FileBinaryContentRepository.class,
                context.getBean(BinaryContentRepository.class)
        );
        assertInstanceOf(
                UserServiceImpl.class,
                context.getBean(UserControllerService.class)
        );
        assertInstanceOf(
                AuthServiceImpl.class,
                context.getBean(AuthControllerService.class)
        );
        assertInstanceOf(
                ChannelServiceImpl.class,
                context.getBean(ChannelControllerService.class)
        );
        assertInstanceOf(
                MessageServiceImpl.class,
                context.getBean(MessageControllerService.class)
        );
        assertInstanceOf(
                ReadStatusServiceImpl.class,
                context.getBean(ReadStatusControllerService.class)
        );
        assertInstanceOf(
                UserStatusServiceImpl.class,
                context.getBean(UserStatusControllerService.class)
        );
        assertInstanceOf(
                InternalBinaryContentServiceImpl.class,
                context.getBean(InternalBinaryContentService.class)
        );
    }

    @Test
    void oneImplementationProvidesControllerAndInternalRoleInterfaces() {
        assertSame(
                context.getBean(MessageControllerService.class),
                context.getBean(InternalMessageService.class)
        );
        assertSame(
                context.getBean(UserStatusControllerService.class),
                context.getBean(InternalUserStatusService.class)
        );
    }
}
