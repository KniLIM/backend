package com.knilim.session.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class DH {

    private static final String KEY_ALGORITHM = "DH";

    private static final String SELECT_ALGORITHM = "AES";

    // 密钥长度
    private static final int KEY_SIZE = 512;

    //公钥
    private static final String PUBLIC_KEY = "DHPublicKey";
    //私钥
    private static final String PRIVATE_KEY = "DHPrivateKey";

    private static final Logger logger = LoggerFactory.getLogger(DH.class);


    /**
     * 根据客户端公钥 {@code key} 初始化服务器端公私钥map
     *
     * @param key 客户端公钥
     * @return Map 服务器密钥Map
     * @throws Exception 生成异常
     */
    public static Map<String, Object> initKey(byte[] key) throws Exception {
        //解析甲方公钥
        //转换公钥材料
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //由甲方公钥构建乙方密钥
        DHParameterSpec dhParameterSpec = ((DHPublicKey) pubKey).getParams();
        //实例化密钥对生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥对生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //产生密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //乙方公钥
        DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();
        //乙方私约
        DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();
        //将密钥对存储在Map中
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }


    /**
     * 构建密钥
     *
     * @param publicKey  公钥
     * @param privateKey 私钥
     * @return byte[] 本地密钥
     */
    public static byte[] getSecretKey(byte[] publicKey, byte[] privateKey) throws Exception {
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //初始化私钥
        //密钥材料转换
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        //产生私钥
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //实例化
        KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
        //初始化
        keyAgree.init(priKey);
        keyAgree.doPhase(pubKey, true);
        //生成本地密钥
        SecretKey secretKey = keyAgree.generateSecret(SELECT_ALGORITHM);
        return secretKey.getEncoded();
    }

    /**
     * 取得私钥字节流
     *
     * @param keyMap 密钥Map
     * @return byte[] 私钥
     */
    public static byte[] getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }

    /**
     * 取得公钥字节流
     *
     * @param keyMap 密钥Map
     * @return byte[] 公钥
     */
    public static byte[] getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }


    /**
     * 根据前端传输过来的 {@code clientPublicKey} 生成本地 密钥
     *
     * @param clientPublicKey 客户端公钥
     * @return String 服务器端密钥 或 错误
     */
    public static String initSecretKey(String clientPublicKey){
        try {
            byte[] clientKey = clientPublicKey.getBytes();
            Map<String, Object> keyMap = initKey(clientKey);
            byte[] secretKey = getSecretKey(getPublicKey(keyMap),getPrivateKey(keyMap));
            String key = new String(secretKey);
            logger.debug(key);
            return key;
        }catch (Exception e) {
            logger.error(e.getMessage());
            return "error";
        }
    }
}