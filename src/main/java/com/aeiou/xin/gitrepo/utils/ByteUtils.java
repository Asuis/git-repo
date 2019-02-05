package com.aeiou.xin.gitrepo.utils;

/**
 * @author asuis
 * @version: ByteUtils.java 1/26/19:2:22 PM
 */
public class ByteUtils {
    public static byte[] merge(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }
}
