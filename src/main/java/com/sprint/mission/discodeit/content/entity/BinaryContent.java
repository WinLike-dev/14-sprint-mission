package com.sprint.mission.discodeit.content.entity;

import com.sprint.mission.discodeit.common.exception.exceptions.InvalidValueException;
import com.sprint.mission.discodeit.common.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 바이너리 콘텐츠 도메인 엔티티.
 * 이미지, 문서 등 파일 첨부물의 메타 정보(파일명, 크기, 유형)만 표현한다.
 * 실제 바이트 데이터는 BinaryContentStorage가 id를 키로 따로 저장한다.
 * setter가 없고 모든 컬럼이 updatable = false이므로 한 번 저장되면 변경할 수 없다.
 * 수정되지 않으므로 updatedAt이 없는 BaseEntity를 상속한다.
 */
@Getter
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 조회 결과를 담을 때 사용한다
public class BinaryContent extends BaseEntity {

    // binary_contents 테이블의 컬럼 길이. @Column과 길이 검증이 같은 값을 쓴다.
    private static final int FILE_NAME_MAX_LENGTH = 255;
    private static final int CONTENT_TYPE_MAX_LENGTH = 100;

    @Column(nullable = false, updatable = false, length = FILE_NAME_MAX_LENGTH)
    private String fileName;    // 파일 이름 (예: "photo.png")

    @Column(nullable = false, updatable = false)
    private long size;          // 파일 크기 (바이트 단위)

    @Column(nullable = false, updatable = false, length = CONTENT_TYPE_MAX_LENGTH)
    private String contentType; // MIME 타입 (예: "image/png")

    // 새 BinaryContent를 생성할 때 사용하는 공개 생성자 (id와 createdAt은 저장 시 부여)
    public BinaryContent(String fileName, long size, String contentType) {
        // 파일 메타 정보는 multipart로 들어와 요청 DTO의 @Size를 거치지 않으므로 길이도 여기서 검사한다.
        this.fileName = requireMaxLength(requireNonBlank(fileName, "fileName"), FILE_NAME_MAX_LENGTH, "fileName");
        this.size = requirePositive(size, "size");
        this.contentType = requireMaxLength(requireNonBlank(contentType, "contentType"), CONTENT_TYPE_MAX_LENGTH, "contentType");
    }

    // 문자열이 null이거나 공백만 있으면 예외를 던지는 유효성 검증 헬퍼 메서드
    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException(fieldName + "은(는) 비어 있을 수 없습니다.");
        }
        return value;
    }

    // 컬럼 길이를 넘는 값은 insert/update가 DB에서 실패해 500이 되므로, 엔티티가 먼저 400으로 거절한다.
    private static String requireMaxLength(String value, int maxLength, String fieldName) {
        if (value.length() > maxLength) {
            throw new InvalidValueException(fieldName + "은(는) " + maxLength + "자를 넘을 수 없습니다.");
        }
        return value;
    }

    // 값이 0 이하이면 예외를 던지는 유효성 검증 헬퍼 메서드
    // 빈 파일은 컨트롤러의 매퍼에서 걸러지므로 저장되는 파일은 항상 1바이트 이상이다.
    private static long requirePositive(long value, String fieldName) {
        if (value <= 0) {
            throw new InvalidValueException(fieldName + "은(는) 0보다 커야 합니다.");
        }
        return value;
    }
}
