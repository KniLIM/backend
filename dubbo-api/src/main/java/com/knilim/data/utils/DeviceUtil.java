package com.knilim.data.utils;

public class DeviceUtil {

    public static Device fromString(String device) {
        switch (device) {
            case "phone":
                return Device.D_PHONE;
            case "tablet":
                return Device.D_TABLET;
            case "pc":
                return Device.D_PC;
            case "watch":
                return Device.D_WATCH;
            default:
                return Device.D_WEB;
        }
    }
}
