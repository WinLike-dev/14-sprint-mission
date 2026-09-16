package com.sprint.mission.discodeit.message.entity;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.common.exception.exceptions.InvalidValueException;
import com.sprint.mission.discodeit.common.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.content.entity.BinaryContent;
import com.sprint.mission.discodeit.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 메시지 도메인 엔티티.
 * 채팅 채널에서 사용자가 보내는 하나의 메시지를 표현한다.
 * 메시지 본문(content), 보낸 채널(channel), 작성자(author), 첨부파일 목록(attachments)을 가진다.
 * BaseUpdatableEntity를 상속받아 id, 생성일시, 수정일시를 저장 시점에 관리한다.
 */
@Getter
@Entity
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 조회 결과를 담을 때 사용한다
public class Message extends BaseUpdatableEntity {

    @Column(columnDefinition = "text")
    private String content;           // 메시지 본문 텍스트

    // 부모 Channel을 가리키는 단방향 N:1. 채널은 바뀌지 않으므로 updatable = false.
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // @ManyToOne 기본값은 EAGER라 명시한다
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    private Channel channel;          // 이 메시지가 속한 채널

    // 작성자는 생명주기상 부모가 아니다. 작성자가 탈퇴해도 메시지는 남고 author만 null이 된다.
    // 그래서 optional 기본값(true)을 쓰고, 값을 비울 수 있어야 하므로 updatable을 막지 않는다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;              // 이 메시지를 작성한 사용자. 작성자가 탈퇴하면 null

    // 부모 Message가 첨부파일의 생명주기를 책임진다.
    // 메시지를 저장·삭제하면 첨부도 함께 저장·삭제되고, 목록에서 빠진 첨부는 고아로 삭제된다.
    // 첨부 하나는 메시지 하나에만 속하므로 조인 테이블의 attachment_id에 UK를 건다.
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id", unique = true)
    )
    @Getter(AccessLevel.NONE) // 내부 리스트를 그대로 내보내지 않도록 getter를 직접 구현한다
    private List<BinaryContent> attachments = new ArrayList<>(); // 첨부된 파일 목록

    // 새 메시지를 생성할 때 사용하는 공개 생성자
    public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        this.content = requireNonBlank(content);
        this.channel = Objects.requireNonNull(channel, "channel은 null일 수 없습니다.");
        this.author = Objects.requireNonNull(author, "author는 null일 수 없습니다.");
        // Hibernate가 저장 시 컬렉션을 감싸므로 불변 리스트가 아닌 가변 리스트로 복사한다.
        this.attachments = new ArrayList<>(Objects.requireNonNull(attachments));
    }

    // 외부에서 첨부 목록을 바꾸지 못하도록 읽기 전용 복사본을 반환한다
    public List<BinaryContent> getAttachments() {
        return List.copyOf(attachments);
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
