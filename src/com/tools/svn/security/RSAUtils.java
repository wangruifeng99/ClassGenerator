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
     * ���������Կ
     * @param length 128 192 256
     */
    public static byte[] genKey(int length) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(length);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * ����
     * @param text ԭ��
     * @return ����
     */
    public static String encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = new SecretKeySpec(SecurityConstant.RSA_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        // AES���ܲ���pkcs5padding���
        Cipher cipher = Cipher.getInstance("AES/ECB/pkcs5padding");
        //���ܳ׳�ʼ��Cipher����
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        //ִ�м��ܲ���
        byte[] encryptData = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptData);
    }

    /**
     * ����
     * @param text ����
     * @return ����
     */
    public static String decrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secretKey = new SecretKeySpec(SecurityConstant.RSA_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/pkcs5padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // ��������, ��������
        byte[] a = cipher.doFinal(Base64.getDecoder().decode(text));
        return new String(a, StandardCharsets.UTF_8);
    }
}
