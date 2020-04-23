package com.knilim.service;

import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.Device;

import java.util.Map;

public interface OnlineService {

    /**
     * 为 Id 为 {@code userId} 的用户添加一台在线设备
     * @param userId 指定的用户
     * @param device 指定的设备
     * @param token 对应设备的 token
     * @param ip 对应设备连接的 session server 的 ip
     * @param port 对应设备连接的 session server 的端口
     */
    void addOnlineDevice(String userId, Device device, String token,
                         String ip, Integer port);

    /**
     * 移除 Id 为 {@code userId} 的用户的设备 {@code device}
     * @param userId 指定的用户
     * @param device 指定的设备
     */
    void removeOnlineDevice(String userId, Device device);

    /**
     * 验证设备的 {@code token} 是否合法
     * @param userId 指定的用户
     * @param device 指定的设备
     * @param token 验证的 token
     * @return 当且晋档用户存在，且状态为 disconnected 并验证 token 成功时返回 true，否则返回 false
     */
    boolean checkToken(String userId, Device device, String token);

    /**
     * 将 Id 为 {@code userId} 的用户的设备 {@code device} 的状态改为 connected
     * @param userId 指定的用户
     * @param device 指定的设备
     * @return 当且仅当用户存在，且状态为 disconnected 并成功改变了状态时返回 true，否则返回 false
     */
    boolean connect(String userId, Device device);

    /**
     * 将 Id 为 {@code userId} 的用户的设备 {@code device} 的状态改为 disconnected
     * @param userId 指定的用户
     * @param device 指定的设备
     * @return 当且仅当用户存在，且状态为 connected 并成功改变了状态时返回 true，否则返回 false
     */
    boolean disconnect(String userId, Device device);

    /**
     * 查找 Id 为 {@code userId} 的用户的设备 {@code device} 的信息
     * @param userId 查找用户
     * @param device 查找设备
     * @return 已连接的设备信息 {@link DeviceInfo}
     */
    DeviceInfo getDevice(String userId, Device device);

    /**
     * 获取 Id 为 {@code Id} 的用户的所有设备
     * @param userId 指定的设备
     * @return 所有已连接的 {@link Device} 和 {@link DeviceInfo>} 的键值对
     *      若用户没有任何设备在线返回 {@code null}
     */
    Map<Device, DeviceInfo> getDevicesByUserId(String userId);
}
