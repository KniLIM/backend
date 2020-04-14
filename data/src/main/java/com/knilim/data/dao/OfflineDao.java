package com.knilim.data.dao;

import java.util.List;
import java.util.UUID;

public interface OfflineDao {

    List<Byte[]> getOfflineMsgs(UUID userId);

    void addOfflineMsg(UUID userId, Byte[] msg);
}
