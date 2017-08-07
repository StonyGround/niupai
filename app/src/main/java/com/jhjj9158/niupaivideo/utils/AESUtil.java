package com.jhjj9158.niupaivideo.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
//import java.net.URLDecoder;
//import android.util.Base64;



import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESUtil {

    /**
     * 加密
     * @param content 需要加密的内容
     * @param password  加密密码
     * @return
     */
    private static String Key="192c96beaec59d367329c70016e7a50f";

    public static String encode(String stringToEncode) throws NullPointerException {

        try {
            SecretKeySpec skeySpec = getKey(Key);
            byte[] clearText = stringToEncode.getBytes("UTF8");
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
            String encrypedValue = Base64.encode(cipher.doFinal(clearText));
            return encrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static SecretKeySpec getKey(String password) throws UnsupportedEncodingException {
        int keyLength = 256;
        byte[] keyBytes = new byte[keyLength / 8];
        Arrays.fill(keyBytes, (byte) 0x0);
        byte[] passwordBytes = password.getBytes("UTF-8");
        int length = passwordBytes.length < keyBytes.length ? passwordBytes.length : keyBytes.length;
        System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }
    /**
     * 解密
     * @return
     */
    public static String decode(String stringToEncode) throws NullPointerException {
        try {
            SecretKeySpec skeySpec = getKey(Key);
//            String utf_8Str = URLDecoder.decode(stringToEncode, "UTF-8");
//            String decode_stringToEncode = new String(Base64.decode(stringToEncode));

//            byte[] clearText = stringToEncode.getBytes();
//            byte[] clearText = stringToEncode.getBytes("UTF8");
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
            String encrypedValue =  new String(cipher.doFinal(Base64.decode(stringToEncode)));
            return  encrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";

    }
}