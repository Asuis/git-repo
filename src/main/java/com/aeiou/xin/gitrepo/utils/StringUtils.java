package com.aeiou.xin.gitrepo.utils;

import java.nio.charset.Charset;

/**
 * @author asuis
 * @version: StringUtils.java 1/25/19:10:04 PM
 */
public class StringUtils {
    public static String byteToString(byte[] bytes) {
        return new String(bytes, Charset.defaultCharset());
    }
}
