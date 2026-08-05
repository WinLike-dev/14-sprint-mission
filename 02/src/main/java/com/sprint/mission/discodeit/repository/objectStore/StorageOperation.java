package com.sprint.mission.discodeit.repository.objectStore;

public enum StorageOperation {
    SAVE("save"),
    LOAD("load"),
    LOAD_ALL("loadAll"),
    DELETE("delete");

    private final String value;

    StorageOperation(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public static StorageOperation fromValue(String value){
        for (StorageOperation operation : values()) {
            if (operation.value.equalsIgnoreCase(value)){
                return operation;
            }
        }
        throw new IllegalArgumentException(
                "지원하지 않는 storage operation입니다: " + value
        );
    }


}
