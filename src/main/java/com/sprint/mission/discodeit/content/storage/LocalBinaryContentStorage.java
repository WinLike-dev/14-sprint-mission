package com.sprint.mission.discodeit.content.storage;

import com.sprint.mission.discodeit.content.service.dto.BinaryContentResult;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

/**
 * 로컬 디스크에 바이너리 데이터를 저장하는 BinaryContentStorage 구현체.
 * 파일 경로 규칙은 {root}/{BinaryContent id} 이다.
 * Bean 등록은 BinaryContentStorageConfig가 discodeit.storage.type=local 일 때만 한다.
 */
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root; // 파일을 저장할 루트 디렉터리

    // Spring 설정 키를 모르고 경로만 받는다. 테스트에서는 임시 디렉터리를 넘겨 바로 만들 수 있다.
    public LocalBinaryContentStorage(Path root) {
        this.root = Objects.requireNonNull(root, "root는 null일 수 없습니다.").toAbsolutePath().normalize();
    }

    // Bean이 만들어진 직후 Spring이 호출해 루트 디렉터리를 준비한다.
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException exception) {
            throw new UncheckedIOException("스토리지 루트 디렉터리를 만들지 못했습니다. root=" + root, exception);
        }
    }

    // 임시 파일에 다 쓴 뒤 최종 이름으로 옮긴다.
    // 바로 최종 파일에 쓰다가 실패하면 반쯤 쓰인 파일이 정상 파일처럼 읽힐 수 있기 때문이다.
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes는 null일 수 없습니다.");
        Path target = resolvePath(binaryContentId);
        Path temp = null;
        try {
            temp = Files.createTempFile(root, binaryContentId + "-", ".tmp");
            Files.write(temp, bytes);
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE);
            return binaryContentId;
        } catch (IOException exception) {
            deleteQuietly(temp, exception);
            throw new UncheckedIOException("파일을 저장하지 못했습니다. id=" + binaryContentId, exception);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            return Files.newInputStream(resolvePath(binaryContentId));
        } catch (IOException exception) {
            throw new UncheckedIOException("파일을 읽지 못했습니다. id=" + binaryContentId, exception);
        }
    }

    // 스트림은 응답을 쓰고 난 뒤 Spring이 닫는다.
    @Override
    public ResponseEntity<Resource> download(BinaryContentResult binaryContent) {
        Objects.requireNonNull(binaryContent, "binaryContent는 null일 수 없습니다.");
        Resource resource = new InputStreamResource(get(binaryContent.id()));
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(binaryContent.fileName(), StandardCharsets.UTF_8) // 한글 파일명도 깨지지 않게 인코딩한다
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(toMediaType(binaryContent.contentType()))
                .contentLength(binaryContent.size())
                .body(resource);
    }

    // put, get이 같은 경로 규칙을 쓰도록 한곳에서 정한다.
    private Path resolvePath(UUID binaryContentId) {
        return root.resolve(Objects.requireNonNull(binaryContentId, "binaryContentId는 null일 수 없습니다.").toString());
    }

    // 저장된 contentType은 업로드한 클라이언트가 보낸 값이라 형식이 틀릴 수 있다.
    // 그때는 다운로드를 막지 않고 일반 바이너리로 내려준다.
    private static MediaType toMediaType(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (InvalidMediaTypeException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    // 저장 실패 뒤 남은 임시 파일을 지운다. 정리 실패는 원래 예외에 덧붙인다.
    private static void deleteQuietly(Path temp, IOException original) {
        if (temp == null) {
            return;
        }
        try {
            Files.deleteIfExists(temp);
        } catch (IOException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }
}
