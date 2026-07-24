package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.CrudRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelRepository implements CrudRepository<Channel> {

    private static final String FILE_EXTENSION = ".ser";
    private final Path directory;

    public FileChannelRepository() {
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
    public Channel create(Channel channel) {
        Path filePath =
                directory.resolve(channel.getId() + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.CREATE_NEW,
                                     StandardOpenOption.WRITE
                             )
                     )) {
            outputStream.writeObject(channel);
            return channel;
        } catch (FileAlreadyExistsException exception) {
            throw new IllegalStateException(
                    "이미 존재하는 채널입니다: " + channel.getId(),
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "채널 저장에 실패했습니다.",
                    exception
            );
        }
    }

    @Override
    public Channel findById(UUID id) {
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
                    "채널 조회에 실패했습니다: " + id,
                    exception
            );
        }
    }

    @Override
    public List<Channel> findAll() {
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
                    "전체 채널 조회에 실패했습니다.",
                    exception
            );
        }

        return channels;
    }

    @Override
    public Channel update(Channel channel) {
        Path filePath =
                directory.resolve(channel.getId() + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.WRITE,
                                     StandardOpenOption.TRUNCATE_EXISTING
                             )
                     )) {
            outputStream.writeObject(channel);
            return channel;
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "수정할 채널 파일을 찾을 수 없습니다: "
                            + channel.getId(),
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "채널 수정 결과 저장에 실패했습니다: "
                            + channel.getId(),
                    exception
            );
        }
    }

    @Override
    public void deleteById(UUID id) {
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
                    "채널 삭제에 실패했습니다: " + id,
                    exception
            );
        }
    }
}
