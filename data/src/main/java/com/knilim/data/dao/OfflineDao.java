package com.knilim.data.dao;

import java.util.List;
import java.util.UUID;

public interface OfflineDao {

    /**
     * 获取 Id 为 {@code userId} 的用户所有的离线消息
     * @param userId 指定的用户
     * @return 离线消息列表，每个离线消息都是字节流 {@link Byte[]}，若无离线消息返回空列表
     */
    List<Byte[]> getOfflineMsgs(UUID userId);

    /**
     * 在 Id 为 {@code userId} 的用户的离线消息池中添加一条离线消息
     * @param userId 指定的用户
     * @param msg 离线消息字节流
     */
    void addOfflineMsg(UUID userId, Byte[] msg);
}
