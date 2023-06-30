package com.tools.svn;

import com.tools.svn.security.RSAUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SignGen {

    public static void main(String[] args) throws Exception {
        System.out.println(RSAUtils.decrypt(""));
    }
}
