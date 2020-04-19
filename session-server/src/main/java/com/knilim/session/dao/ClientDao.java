package com.knilim.session.dao;

import com.knilim.data.utils.Device;
import com.knilim.session.model.Connect;

import java.util.Map;


public interface ClientDao {

    /**
     * 根据 {@code userId} 获取该用户所有连接本 session server 的 {@code Connect}
     *
     * @param userId 查找的用户
     * @return 用户对应的 {@code Connect} hashmap
     */
    Map<Device, Connect> getConnectsByUserId(String userId);

    /**
     * 根据 {@code userId}与{@code device} 返回 {@link Connect} 对象
     *
     * @param userId 查找的用户
     * @param device 查找的设备
     * @return 对应的 {@link Connect} 对象
     */
    Connect getConnect(String userId,Device device);

    /**
     * 根据 {@code userId}与{@code device} 返回用户加密密钥
     *
     * @param userId 查找的用户
     * @param device 查找的设备
     * @return 对应的 {@code key} 对象
     */
    String getKey(String userId, Device device);

    /**
     * 向 {@code userId} 的 clients 表里添加一个新的 设备 {@code Connect}
     *
     * @param userId    添加的用户
     * @param device    用户设备，枚举见 {@link Device}
     * @param sessionId 用户客户端的 sessionId
     * @param host      用户客户端的 ip
     * @param port      用户客户端的端口
     * @param key       用户客户端的 AES 密钥
     */
    void addConnect(String userId,Device device,String sessionId, String host, Integer port, String key);

    /**
     * 删除 {@code userId} 的 connects 表中的 {@code device}
     *
     * @param userId    删除的用户
     * @param device 删除的 {@code Device}
     */
    void removeConnect(String userId,Device device);



}
