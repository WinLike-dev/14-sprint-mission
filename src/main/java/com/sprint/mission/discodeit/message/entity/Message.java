package com.sprint.mission.discodeit.message.entity;

import com.sprint.mission.discodeit.common.exception.InvalidValueException;
import com.sprint.mission.discodeit.common.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 메시지 도메인 엔티티.
 * 채팅 채널에서 사용자가 보내는 하나의 메시지를 표현한다.
 * 메시지 본문(content), 보낸 채널(channelId), 작성자(authorId), 첨부파일 목록(attachmentIds)을 가진다.
 * BaseUpdatableEntity를 상속받아 id, 생성일시, 수정일시를 저장 시점에 관리한다.
 */
@Getter
@Entity
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 조회 결과를 담을 때 사용한다
public class Message extends BaseUpdatableEntity {

    @Column(columnDefinition = "text")
    private String content;           // 메시지 본문 텍스트

    // 연관관계 매핑 전까지는 FK 컬럼 값만 UUID로 다룬다.
    @Column(nullable = false, updatable = false)
    private UUID channelId;           // 이 메시지가 속한 채널의 ID

    // 작성자가 삭제되면 DB가 NULL로 바꾼다(ON DELETE SET NULL). 그래서 컬럼은 null을 허용한다.
    @Column(updatable = false)
    private UUID authorId;            // 이 메시지를 작성한 사용자의 ID

    // 연관관계 매핑 전까지는 message_attachments 테이블의 attachment_id 값만 모은다.
    @ElementCollection
    @CollectionTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id")
    )
    @Column(name = "attachment_id", nullable = false)
    @Getter(AccessLevel.NONE) // 내부 리스트를 그대로 내보내지 않도록 getter를 직접 구현한다
    private List<UUID> attachmentIds = new ArrayList<>(); // 첨부된 파일(BinaryContent)들의 ID 목록

    // 새 메시지를 생성할 때 사용하는 공개 생성자
    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.content = requireNonBlank(content);
        this.channelId = Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        this.authorId = Objects.requireNonNull(authorId, "authorId는 null일 수 없습니다.");
        // Hibernate가 저장 시 컬렉션을 감싸므로 불변 리스트가 아닌 가변 리스트로 복사한다.
        this.attachmentIds = new ArrayList<>(Objects.requireNonNull(attachmentIds));
    }

    // 외부에서 첨부 목록을 바꾸지 못하도록 읽기 전용 복사본을 반환한다
    public List<UUID> getAttachmentIds() {
        return List.copyOf(attachmentIds);
    }

    // 메시지 본문을 수정한다. 수정 시각은 저장 시점에 BaseUpdatableEntity가 갱신한다
    public void update(String content) {
        this.content = requireNonBlank(content);
    }

    // 문자열이 null이거나 공백만 있으면 예외를 던지는 유효성 검증 헬퍼 메서드
    private static String requireNonBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("content은(는) 비어 있을 수 없습니다.");
        }
        return value;
    }
}
