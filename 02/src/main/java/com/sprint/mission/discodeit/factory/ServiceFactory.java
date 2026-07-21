package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import lombok.Getter;

@Getter
public class ServiceFactory {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    private ServiceFactory() {
        userService = new JCFUserService();
        channelService = new JCFChannelService();
        messageService = new JCFMessageService(userService, channelService);
    }

    private static class LazyHolder {
        private static final ServiceFactory INSTANCE = new ServiceFactory();
    }

    public static ServiceFactory getInstance() {
        return LazyHolder.INSTANCE;
    }
}
