package com.knilim.session.data;

import com.knilim.session.handler.MsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class AESEncryptor {

    private static final Logger logger = LoggerFactory.getLogger(MsgHandler.class);

    public static byte[] encrypt(byte[] input, String key) {
//        try {
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
//            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
//            return cipher.doFinal(input);
//        } catch (Exception e) {
//            logger.error("AES encrypt error: " + e.getMessage());
//            e.printStackTrace();
//            return new byte[0];
//        }
        return input;
    }

    public static byte[] decrypt(byte[] input, String key) {
//        try {
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
//            cipher.init(Cipher.DECRYPT_MODE, keySpec);
//            return cipher.doFinal(input);
//        } catch (Exception e) {
//            logger.error("AES decrypt error: " + e.getMessage());
//            e.printStackTrace();
//            return new byte[0];
//        }
        return input;
    }
}
