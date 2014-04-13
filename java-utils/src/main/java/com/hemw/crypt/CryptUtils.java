package com.hemw.crypt;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 实现加密解密的工具类，目前支持的算法有：消息摘要（MD5, SHA-1）和对称加密算法（DES,DESede, Blowfish）<br>
 * 其他加密算法：DSA（数字签名），Diffie-Hellman（密钥一致协议）。<br>
 * 注意：一个英文字符=1个字节=8个字位，密钥所提及的位指的是字位，即bit，不是字符位数。<br>
 * 创建日期：2012-8-28
 * 
 * @author <a href="mailto:hemw@mochasoft.com.cn">何明旺</a>
 * @version 1.0
 * @since 1.0
 */

public class CryptUtils {
    /** 算法DES要求密钥长度为64位密钥, 有效密钥56位。64bits=8*8*1，即8个ascii字符 */
    public static final CryptUtils Algorithm_DES = new CryptUtils("DES");
    
    /** 算法DESede要求的密钥位数为192位，即192bits=64*3=8*8*3，即24个ascii字符 */
    public static final CryptUtils Algorithm_DESede = new CryptUtils("DESede");

    /** 算法Blowfish要求密钥长度为，密钥8--448字位，即8--448(bits) */
    public static final CryptUtils Algorithm_Blowfish = new CryptUtils("Blowfish");

    /** 消息摘要-MD5算法，加密后是16位 */
    public static final CryptUtils Algorithm_MD5 = new CryptUtils("MD5");
    
    /** 消息摘要-SHA-1，加密后是20位 */
    public static final CryptUtils Algorithm_SHA = new CryptUtils("SHA-1");

    private SecretKey algorithmKey;
    private Cipher cipher;
    private byte[] encryptorData;
    private byte[] decryptorData;

    private String algorithm = null;

    /**
     * 在构造器中初始化使用的算法
     * 
     * @param paramAlgorithm 加密算法名称
     */
    private CryptUtils(String paramAlgorithm) {
        algorithm = paramAlgorithm;
    }

    /**
     * 通过加密的key初始化算法需要的数据
     * 
     * @param paramKey 加密密钥
     */
    private void init(String paramKey) {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        try {
            byte key[] = paramKey.getBytes();
            algorithmKey = new SecretKeySpec(key, algorithm);
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 
     * 将字节数组转化为通过Base64编码后的字符串
     * 
     * @param dataByte byte[] 字节数组
     * @return 通过 Base64 编码后的字符串
     */
    private static String byteToString(byte[] dataByte) {
        return new BASE64Encoder().encode(dataByte);
    }

    /**
     * 
     * 将通过Base64编码的字符串转换为字节数组
     * 
     * @param datasource 通过Base64编码后的字符串
     * @return byte[] 字节数组
     * @throws IOException 
     */
    private static byte[] stringToByte(String datasource) throws IOException {
        return new BASE64Decoder().decodeBuffer(datasource);
    }

    /**
     * 消息摘要算法是不可逆的
     * 
     * @param input 要加密的字符串
     * @return String 加密后的字符串
     * @throws NoSuchAlgorithmException 
     * @throws IOException 
     */
    public String encryptByMD5(String input) throws NoSuchAlgorithmException, IOException  {
        java.security.MessageDigest alg = java.security.MessageDigest.getInstance(algorithm);
        alg.update(stringToByte(input));
        byte[] digest = alg.digest();
        return byteToString(digest);
    }

    /**
     * 对byte[]进行加密，内部使用的方法
     * 
     * @param datasource 要加密的数据
     * @param paramKey 加密密钥
     * @return 返回加密后的 byte 数组
     */
    private byte[] encrypt(byte[] datasource, String paramKey) {
        try {
            init(paramKey);
            cipher.init(Cipher.ENCRYPT_MODE, algorithmKey);
            encryptorData = cipher.doFinal(datasource);
        } catch (java.security.InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (javax.crypto.BadPaddingException ex) {
            ex.printStackTrace();
        } catch (javax.crypto.IllegalBlockSizeException ex) {
            ex.printStackTrace();
        }
        return encryptorData;
    }

    /**
     * 实现字符串加密
     * 
     * @param datasource 要加密的字符串
     * @param paramKey 用来加密的密钥
     * @return 加密后的字符串
     * @throws Exception
     */
    public String encrypt(String datasource, String paramKey) throws Exception {
        String result = "";
        // 对DES的密钥长度的校验
        if (Algorithm_DES.equals(this) && (paramKey != null && paramKey.length() != 8)) 
                throw new RuntimeException("the length of key is not available for DES!");

        if (datasource != null && datasource.trim().length() != 0) {
            byte[] str = encrypt(datasource.getBytes(), paramKey);
            BASE64Encoder be = new BASE64Encoder();
            result = be.encode(str);
        }
        return result;
    }

    /**
     * 对 datasource 数组进行解密
     * 
     * @param datasource 要解密的数据
     * @param paramKey 用来加密的密钥
     * @return 返回加密后的 byte[]
     */
    private byte[] decrypt(byte[] datasource, String paramKey) {
        try {
            init(paramKey);
            cipher.init(Cipher.DECRYPT_MODE, algorithmKey);
            decryptorData = cipher.doFinal(datasource);
        } catch (java.security.InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (javax.crypto.BadPaddingException ex) {
            ex.printStackTrace();
        } catch (javax.crypto.IllegalBlockSizeException ex) {
            ex.printStackTrace();
        }
        return decryptorData;
    }

    /**
     * 实现字符串解密
     * 
     * @param datasource 要解密密的字符串
     * @param paramKey 用来解密的密钥
     * @return 解密后的字符串
     * @throws Exception
     */
    public String decrypt(String encryptedStr, String paramKey) throws IOException {
        String result = "";
        if (encryptedStr != null && encryptedStr.trim().length() != 0) {
            BASE64Decoder bd = new BASE64Decoder();
            byte[] sorData = bd.decodeBuffer(encryptedStr);
            sorData = this.decrypt(sorData, paramKey);
            result = new String(sorData);
        }
        return result;
    }
}
