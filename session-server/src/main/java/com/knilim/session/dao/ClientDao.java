package com.knilim.session.dao;

import java.util.ArrayList;
import java.util.UUID;

public interface ClientDao {

    /**
     * 根据 {@code userId} 获取该用户所有连接本 session server 的 {@code sessionId}
     * @param userId 查找的用户
     * @return 用户对应的 {@code sessionId} 列表
     */
    ArrayList<UUID> getClientsByUserId(UUID userId);


    /**
     * 向 {@code userId} 的 clients 表里添加一个新的 {@code sessionId}
     * @param userId 添加的用户
     * @param sessionId 要添加的 {@code sessionId}
     */
    void addClient(UUID userId, UUID sessionId);

    /**
     * 删除 {@code userId} 的 clients 表中的 {@code sessionId}
     * @param userId 删除的用户
     * @param sessionId 删除的 {@code sessionId}
     */
    void removeClient(UUID userId, UUID sessionId);
}
