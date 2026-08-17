package com.sprint.mission.discodeit.content.domain.binarycontent;

import com.sprint.mission.discodeit.common.entity.Identifiable;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * 바이너리 콘텐츠 도메인 엔티티.
 * 이미지, 문서 등 파일 첨부물의 메타데이터와 실제 바이너리 데이터를 함께 보관한다.
 * 모든 필드가 final이므로 한 번 생성되면 변경할 수 없는 불변 객체이다.
 * byte 배열은 방어적 복사를 통해 외부에서 내부 데이터를 변경할 수 없도록 보호한다.
 */
@Getter
public final class BinaryContent implements Identifiable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;            // 고유 식별자
    private final Instant createdAt;  // 생성 시각
    private final String fileName;    // 파일 이름 (예: "photo.png")
    private final long size;          // 파일 크기 (바이트 단위)
    private final String contentType; // MIME 타입 (예: "image/png")
    @Getter(lombok.AccessLevel.NONE)  // Lombok이 자동으로 getter를 만들지 않도록 설정 (방어적 복사를 위해 직접 구현)
    private final byte[] bytes;       // 파일의 실제 바이너리 데이터

    // 새 BinaryContent를 생성할 때 사용하는 공개 생성자 (id와 createdAt은 자동 생성)
    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        this(UUID.randomUUID(), Instant.now(), fileName, contentType, bytes);
    }

    // copy() 메서드 전용 비공개 생성자 - 기존 id, 생성일시를 유지하며 복사할 때 사용
    private BinaryContent(
            UUID id,
            Instant createdAt,
            String fileName,
            String contentType,
            byte[] bytes
    ) {
        this.id = Objects.requireNonNull(id, "id는 null일 수 없습니다.");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt은 null일 수 없습니다.");
        this.fileName = requireNonBlank(fileName, "fileName");
        this.contentType = requireNonBlank(contentType, "contentType");
        this.bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length); // 방어적 복사
        this.size = this.bytes.length; // 복사 후 크기 계산
    }

    // 방어적 복사를 통해 내부 byte 배열의 복사본을 반환한다
    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    // JCF 기반 저장소에서 서비스 계층과 데이터를 격리하기 위한 복사 메서드
    public BinaryContent copy() {
        return new BinaryContent(id, createdAt, fileName, contentType, bytes);
    }

    // 문자열이 null이거나 공백만 있으면 예외를 던지는 유효성 검증 헬퍼 메서드
    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "은(는) 비어 있을 수 없습니다.");
        }
        return value;
    }
}
