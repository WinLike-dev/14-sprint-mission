package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private static final String FILE_EXTENSION = ".ser";
    private final Path directory;

    public FileChannelService() {
        directory = Path.of("data", "channels");

        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "채널 저장 디렉터리 준비에 실패했습니다.",
                    exception
            );
        }
    }

    @Override
    public Channel createChannel(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        Path filePath =
                directory.resolve(channel.getId() + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(filePath)
                     )) {
            outputStream.writeObject(channel);
            return channel;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "채널 생성 중 파일 처리에 실패했습니다.",
                    exception
            );
        }
    }

    @Override
    public Channel readChannel(UUID id) {
        Path filePath = directory.resolve(id + FILE_EXTENSION);

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(
                             Files.newInputStream(filePath)
                     )) {
            return (Channel) inputStream.readObject();
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "채널 파일을 찾을 수 없습니다: " + id,
                    exception
            );
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            throw new IllegalStateException(
                    "채널 조회 중 파일 처리에 실패했습니다: " + id,
                    exception
            );
        }
    }

    @Override
    public List<Channel> readAllChannels() {
        List<Channel> channels = new ArrayList<>();

        try (DirectoryStream<Path> filePaths =
                     Files.newDirectoryStream(
                             directory,
                             "*" + FILE_EXTENSION
                     )) {
            for (Path filePath : filePaths) {
                try (ObjectInputStream inputStream =
                             new ObjectInputStream(
                                     Files.newInputStream(filePath)
                             )) {
                    channels.add((Channel) inputStream.readObject());
                }
            }
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            throw new IllegalStateException(
                    "전체 채널 조회 중 파일 처리에 실패했습니다.",
                    exception
            );
        }

        return List.copyOf(channels);
    }

    @Override
    public void updateChannel(UUID id, String name, String description) {
        Channel channel = readChannel(id);
        channel.update(name, description);
        Path filePath = directory.resolve(id + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.WRITE,
                                     StandardOpenOption.TRUNCATE_EXISTING
                             )
                     )) {
            outputStream.writeObject(channel);
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "수정할 채널 파일을 찾을 수 없습니다: " + id,
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "채널 수정 중 파일 처리에 실패했습니다: " + id,
                    exception
            );
        }
    }

    @Override
    public void deleteChannel(UUID id) {
        Path filePath = directory.resolve(id + FILE_EXTENSION);

        try {
            Files.delete(filePath);
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "삭제할 채널 파일을 찾을 수 없습니다: " + id,
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "채널 삭제 중 파일 처리에 실패했습니다: " + id,
                    exception
            );
        }
    }
}
