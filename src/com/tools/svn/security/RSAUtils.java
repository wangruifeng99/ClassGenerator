package com.tools.svn.security;

import com.tools.svn.constant.SecurityConstant;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RSAUtils {

    /**
     * 生成随机密钥
     * @param length 128 192 256
     */
    public static byte[] genKey(int length) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(length);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 加密
     * @param text 原文
     * @return 密文
     */
    public static String encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = new SecretKeySpec(SecurityConstant.RSA_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        // AES加密采用pkcs5padding填充
        Cipher cipher = Cipher.getInstance("AES/ECB/pkcs5padding");
        //用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        //执行加密操作
        byte[] encryptData = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptData);
    }

    /**
     * 解密
     * @param text 密文
     * @return 密文
     */
    public static String decrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = new SecretKeySpec(SecurityConstant.RSA_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/pkcs5padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // 解密数据, 返回明文
        byte[] a = cipher.doFinal(Base64.getDecoder().decode(text));
        return new String(a, StandardCharsets.UTF_8);
    }
}
