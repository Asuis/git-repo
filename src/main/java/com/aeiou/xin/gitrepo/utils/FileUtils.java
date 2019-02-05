package com.aeiou.xin.gitrepo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author asuis
 * @version: FileUtils.java 1/25/19:9:56 PM
 */
public class FileUtils {
    public static File path(String path) {
        return new File(path);
    }

    public static byte[] getFileContent(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFindException();
        }
        if (!file.canRead()) {
            throw new FileNotAuthroizedException();
        }
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
class FileNotFindException extends RuntimeException{}
class FileNotAuthroizedException extends RuntimeException{}