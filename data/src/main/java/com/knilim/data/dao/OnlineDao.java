package com.knilim.data.dao;

import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.Device;

import java.util.Map;
import java.util.UUID;

public interface OnlineDao {

    /**
     * 为 Id 为 {@code userId} 的用户添加一台在线设备
     * @param userId 指定的用户
     * @param device 指定的设备
     * @param token 对应设备的 token
     * @param ip 对应设备连接的 session server 的 ip
     * @param port 对应设备连接的 session server 的端口
     */
    void addOnlineDevice(UUID userId, Device device, String token,
                         String ip, Integer port);

    /**
     * 移除 Id 为 {@code userId} 的用户的设备 {@code device}
     * @param userId 指定的用户
     * @param device 指定的设备
     */
    void removeOnlineDevice(UUID userId, Device device);

    /**
     * 验证设备的 {@code token} 是否合法，若合法则将设备状态改为 connected
     * @param userId 指定的用户
     * @param device 指定的设备
     * @param token 验证的 token
     * @return 验证结果
     */
    boolean checkToken(UUID userId, Device device, String token);

    /**
     * 查找 Id 为 {@code userId} 的用户的设备 {@code device} 的信息
     * @param userId 查找用户
     * @param device 查找设备
     * @return 已连接的设备信息 {@link DeviceInfo}
     */
    DeviceInfo getDevice(UUID userId, Device device);

    /**
     * 获取 Id 为 {@code Id} 的用户的所有设备
     * @param userId 指定的设备
     * @return 所有已连接的 {@link Device} 和 {@link DeviceInfo>} 的键值对
     *      若用户没有任何设备在线返回 {@code null}
     */
    Map<Device, DeviceInfo> getDevicesByUserId(UUID userId);
}
