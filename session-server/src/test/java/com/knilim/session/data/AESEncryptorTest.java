package com.knilim.session.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AESEncryptorTest {

    private static final String data = "hello world";

    private static final String key = "2b7e151628aed2a6abf7158809cf4f3c";

    @Test
    void encrypt() {
        byte[] encrypted = AESEncryptor.encrypt(data.getBytes(), key);
        byte[] decrypted = AESEncryptor.decrypt(encrypted, key);
        assertEquals(new String(decrypted), data);
    }
}
