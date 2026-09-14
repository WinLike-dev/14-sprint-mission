package com.sprint.mission.discodeit.content.entity;

import com.sprint.mission.discodeit.common.exception.InvalidValueException;
import com.sprint.mission.discodeit.common.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * 바이너리 콘텐츠 도메인 엔티티.
 * 이미지, 문서 등 파일 첨부물의 메타데이터와 실제 바이너리 데이터를 함께 보관한다.
 * setter가 없고 모든 컬럼이 updatable = false이므로 한 번 저장되면 변경할 수 없다.
 * 수정되지 않으므로 updatedAt이 없는 BaseEntity를 상속한다.
 * byte 배열은 방어적 복사를 통해 외부에서 내부 데이터를 변경할 수 없도록 보호한다.
 */
@Getter
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 조회 결과를 담을 때 사용한다
public class BinaryContent extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String fileName;    // 파일 이름 (예: "photo.png")

    @Column(nullable = false, updatable = false)
    private long size;          // 파일 크기 (바이트 단위)

    @Column(nullable = false, updatable = false, length = 100)
    private String contentType; // MIME 타입 (예: "image/png")

    // 메타 정보와 바이너리 분리(BinaryContentStorage) 전까지는 bytea 컬럼에 함께 저장한다.
    @Column(nullable = false, updatable = false)
    @Getter(AccessLevel.NONE)  // Lombok이 자동으로 getter를 만들지 않도록 설정 (방어적 복사를 위해 직접 구현)
    private byte[] bytes;       // 파일의 실제 바이너리 데이터

    // 새 BinaryContent를 생성할 때 사용하는 공개 생성자 (id와 createdAt은 저장 시 부여)
    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        this.fileName = requireNonBlank(fileName, "fileName");
        this.contentType = requireNonBlank(contentType, "contentType");
        this.bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length); // 방어적 복사
        this.size = this.bytes.length; // 복사 후 크기 계산
    }

    // 방어적 복사를 통해 내부 byte 배열의 복사본을 반환한다
    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    // 문자열이 null이거나 공백만 있으면 예외를 던지는 유효성 검증 헬퍼 메서드
    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException(fieldName + "은(는) 비어 있을 수 없습니다.");
        }
        return value;
    }
}
