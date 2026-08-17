package com.sprint.mission.discodeit.message.domain.message;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 메시지 도메인 엔티티.
 * 채팅 채널에서 사용자가 보내는 하나의 메시지를 표현한다.
 * 메시지 본문(content), 보낸 채널(channelId), 작성자(authorId), 첨부파일 목록(attachmentIds)을 가진다.
 * BaseEntity를 상속받아 id, 생성일시, 수정일시를 자동으로 관리한다.
 */
@Getter
public class Message extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private String content;           // 메시지 본문 텍스트
    private final UUID channelId;     // 이 메시지가 속한 채널의 ID
    private final UUID authorId;      // 이 메시지를 작성한 사용자의 ID
    private final List<UUID> attachmentIds; // 첨부된 파일(BinaryContent)들의 ID 목록

    // 새 메시지를 생성할 때 사용하는 공개 생성자
    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.content = requireNonBlank(content);
        this.channelId = Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        this.authorId = Objects.requireNonNull(authorId, "authorId는 null일 수 없습니다.");
        this.attachmentIds = List.copyOf(Objects.requireNonNull(attachmentIds)); // 불변 리스트로 복사하여 외부 변경 방지
    }

    // copy() 메서드 전용 비공개 생성자 - 기존 id, 생성일시, 수정일시를 그대로 유지하며 복사할 때 사용
    private Message(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String content,
            UUID channelId,
            UUID authorId,
            List<UUID> attachmentIds
    ) {
        super(id, createdAt, updatedAt);
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = List.copyOf(attachmentIds);
    }

    // 메시지 본문을 수정하고, 수정 시각을 갱신한다
    public void update(String content) {
        this.content = requireNonBlank(content);
        markUpdated(); // BaseEntity의 updatedAt을 현재 시각으로 갱신
    }

    // 카피 오퍼레이터로 객체 그대로 복사 목표는 JCF의 저장소와 서비스 데이터 격리 둘 다 메모리 안이라 격리해야함
    public Message copy() {
        return new Message(
                getId(),
                getCreatedAt(),
                getUpdatedAt(),
                content,
                channelId,
                authorId,
                attachmentIds
        );
    }

    // 문자열이 null이거나 공백만 있으면 예외를 던지는 유효성 검증 헬퍼 메서드
    private static String requireNonBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("content은(는) 비어 있을 수 없습니다.");
        }
        return value;
    }
}
