package com.tools.svn;

import com.tools.svn.security.RSAUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SignGen {

    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        System.out.println(RSAUtils.encrypt("testuser1"));
        System.out.println(RSAUtils.encrypt("testapp1"));
        System.out.println(RSAUtils.encrypt("cplapp1"));
        System.out.println(RSAUtils.encrypt("hbapp1"));
        System.out.println(RSAUtils.encrypt("algoapp1"));
    }
}
