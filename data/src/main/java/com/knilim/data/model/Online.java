package com.knilim.data.model;

import com.knilim.data.utils.Device;
import org.springframework.data.annotation.Id;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Online {

    @Id
    private UUID userId;

    private Map<Device, DeviceInfo> devices;

    public Online(UUID userId) {
        this.userId = userId;
        this.devices = new HashMap<>();
    }

    public UUID getUserId() {
        return userId;
    }

    public DeviceInfo getDevice(Device device) {
        return this.devices.get(device);
    }

    public Map<Device, DeviceInfo> getDevices() {
        return devices;
    }

    public void addDevice(Device device, String token, String ip, Integer port) {
        this.devices.put(device, new DeviceInfo(token, ip, port));
    }

    public void removeDevice(Device device) {
        this.devices.remove(device);
    }
}
