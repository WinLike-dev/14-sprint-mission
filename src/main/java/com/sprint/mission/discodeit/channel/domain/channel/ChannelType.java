package com.sprint.mission.discodeit.channel.domain.channel;

/**
 * 채널의 유형을 나타내는 열거형(enum).
 * PUBLIC: 누구나 볼 수 있는 공개 채널
 * PRIVATE: 초대된 사람만 볼 수 있는 비공개 채널 (DM)
 * enum을 사용하면 채널 유형을 이 두 값으로만 제한할 수 있어서, 잘못된 문자열이 들어오는 것을 방지한다.
 */
// 문법: enum은 채널 유형을 두 유효 값으로 제한해 잘못된 문자열 상태를 막는다.
public enum ChannelType {
    PUBLIC,  // 공개 채널 - 이름과 설명이 있고, 모든 사용자가 접근 가능
    PRIVATE  // 비공개 채널(DM) - 이름/설명 없이 참여자 목록으로만 구분
}
