package com.sprint.mission.discodeit.repository.objectStore;

// 실패 단계
public enum StorageStage {
    CREATE_TEMP_FILE("임시 파일 생성"),
    SERIALIZE("직렬화"),
    MOVE_FILE("파일 이동"),
    OPEN_DIRECTORY("저장 디렉터리 열기"),
    ITERATE_DIRECTORY("저장 파일 목록 순회"),
    PARSE_FILE_NAME("파일명에서 식별자 추출"),
    DESERIALIZE("역직렬화"),
    DELETE_FILE("저장 파일 삭제");

    private final String description;

    StorageStage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
