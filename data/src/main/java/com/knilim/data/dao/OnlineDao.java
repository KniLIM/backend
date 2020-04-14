package com.knilim.data.dao;

import com.knilim.data.model.DeviceInfo;
import com.knilim.data.utils.Device;

import java.util.Map;
import java.util.UUID;

public interface OnlineDao {

    void addOnlineDevice(UUID userId, Device device, String token, String ip, Integer port);

    void removeOnlineDevice(UUID userId, Device device);

    /**
     * 查找 Id 为 {@code userId} 的用户的设备 {@code device} 的信息
     * @param userId 查找用户
     * @param device 查找设备
     * @return 设备信息 {@link DeviceInfo}
     */
    DeviceInfo getDevice(UUID userId, Device device);

    Map<Device, DeviceInfo> getDevicesByUserId(UUID userId);
}
