package com.knilim.session.dao;

import com.knilim.session.model.Connect;
import com.knilim.data.utils.Device;

import java.util.UUID;

public interface ConnectDao {

    /**
     * 根据 {@code sessionId} 返回 {@link Connect} 对象
     * @param sessionId 查找会话的 Id
     * @return {@code sessionId} 对应的 {@link Connect} 对象
     */
    Connect getConnectBySessionId(UUID sessionId);

    /**
     * 添加一个新的 {@link Connect} 对象
     * @param sessionId 会话 Id
     * @param userId 用户 Id
     * @param device 用户设备，枚举见 {@link Device}
     * @param host 用户客户端的 ip
     * @param port 用户客户端的端口
     * @param key 用户客户端的 AES 密钥
     */
    void addConnect(UUID sessionId, UUID userId, Device device,
                    String host, Integer port, String key);

    /**
     * 删除 ID 为 {@code sessionId} 的 {@link Connect}
     * @param sessionId 删除会话的 Id
     */
    void removeConnect(UUID sessionId);
}
