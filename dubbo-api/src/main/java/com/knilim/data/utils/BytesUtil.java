package com.knilim.data.utils;

public class BytesUtil {
    public static Byte[] toObj(byte[] byteArray) {
        Byte[] bytes = new Byte[byteArray.length];

        for (int i = 0; i < byteArray.length; ++i) {
            bytes[i] = byteArray[i];
        }

        return bytes;
    }

    public static byte[] toPrimitives(Byte[] byteArray) {
        byte[] bytes = new byte[byteArray.length];

        for (int i = 0; i < byteArray.length; ++i) {
            bytes[i] = byteArray[i];
        }

        return bytes;
    }
}
