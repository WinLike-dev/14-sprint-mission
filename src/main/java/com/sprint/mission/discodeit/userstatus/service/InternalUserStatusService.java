package com.sprint.mission.discodeit.userstatus.service;

import java.util.UUID;

public interface InternalUserStatusService {

    void createForUser(UUID userId);

    void deleteByUserId(UUID userId);
}
