package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.NoSuchElementException;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        ServiceFactory factory = ServiceFactory.getInstance();
        UserService userService = factory.getUserService();
        ChannelService channelService = factory.getChannelService();
        MessageService messageService = factory.getMessageService();

        User sender = userService.createUser("sender", "sender@email.com", "1234");
        User receiver = userService.createUser("receiver", "receiver@email.com", "1234");
        System.out.println(userService.readUser(sender.getId()).getUsername());
        System.out.println("사용자 수: " + userService.readAllUsers().size());
        userService.updateUser(sender.getId(), "new-sender", "new@email.com", "5678");
        System.out.println(userService.readUser(sender.getId()).getUsername());

        Channel channel = channelService.createChannel(ChannelType.PUBLIC, "general", "general channel");
        System.out.println(channelService.readChannel(channel.getId()).getName());
        System.out.println("채널 수: " + channelService.readAllChannels().size());
        channelService.updateChannel(channel.getId(), "notice", "notice channel");
        System.out.println(channelService.readChannel(channel.getId()).getName());

        Message message = messageService.createMessage("hello", channel.getId(), sender.getId(), receiver.getId());
        System.out.println(messageService.readMessage(message.getId()).getContent());
        System.out.println("메시지 수: " + messageService.readAllMessages().size());
        messageService.updateMessage(message.getId(), "hello, world");
        System.out.println(messageService.readMessage(message.getId()).getContent());

        try {
            messageService.createMessage("invalid", channel.getId(), UUID.randomUUID(), receiver.getId());
        } catch (NoSuchElementException e) {
            System.out.println("연관 사용자 검증 성공: " + e.getMessage());
        }

        messageService.deleteMessage(message.getId());
        channelService.deleteChannel(channel.getId());
        userService.deleteUser(sender.getId());
        userService.deleteUser(receiver.getId());

        System.out.println("메시지 삭제 후: " + messageService.readAllMessages());
        System.out.println("채널 삭제 후: " + channelService.readAllChannels());
        System.out.println("사용자 삭제 후: " + userService.readAllUsers());
        System.out.println("싱글톤 확인: " + (factory == ServiceFactory.getInstance()));
    }
}
