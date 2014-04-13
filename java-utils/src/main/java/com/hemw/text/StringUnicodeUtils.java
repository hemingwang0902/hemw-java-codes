package com.hemw.text;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * String 编码与解码的工具类<br>
 * 创建日期：2012-5-23
 * @author  <a href="mailto:hemw@mochasoft.com.cn">何明旺</a>
 * @version 1.0
 * @see
 * @since 1.0
 */
public class StringUnicodeUtils {
    public static final String ENCODING_UTF8 = "UTF-8";
    public static final String ENCODING_GBK = "GBK";

    /**
     * 将字符串转换成 Unicode编码方式
     * @param text 需要转码的字符串
     * @param encoding 编码字符集
     * @return 编码后的字符串，如果发生 {@link UnsupportedEncodingException} 异常，则返回 -1， 如果发生 {@link IOException} 异常，则返回 -2
     */
    public static String textToUnicode(String text, String encoding) {
        String result = "";
        int input;
        StringReader reader = null;
        try {
            reader = new StringReader(new String(text.getBytes(), encoding));
            while ((input = reader.read()) != -1) {
                result += ("\\u" + Integer.toHexString(input));
            }
        } catch (UnsupportedEncodingException e) {
            return "-1";
        } catch (IOException e) {
            return "-2";
        } finally {
            if (reader != null)
                reader.close();
        }
        return result;
    }

    /**
     * 将Unicode编码方式的字符串转为指定编码类型
     * @param unicode 需要转换的 Unicode 编码字符串
     * @param encoding 编码字符集
     * @return 转码后的字符串，如果发生 {@link UnsupportedEncodingException} 异常，则返回 <code>null</code>
     */
    public static String UnicodeToText(String unicode, String encoding) {
        try {
            return new String(unicode.getBytes(encoding), encoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(textToUnicode("在这个感恩的季节", "UTF-8"));
    }
}
