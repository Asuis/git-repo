package com.aeiou.xin.gitrepo.service;

import com.aeiou.xin.gitrepo.utils.ByteUtils;
import com.aeiou.xin.gitrepo.utils.CommandUtils;
import com.aeiou.xin.gitrepo.utils.FileUtils;
import com.aeiou.xin.gitrepo.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author asuis
 * @version: GitService.java 1/25/19:9:42 PM
 */
public class GitService {

    private final static Logger logger = LoggerFactory.getLogger(GitService.class);

    public static byte[] getRefs(String path, String service) {
        logger.info("get refs path:", path);
        byte[] refs = CommandUtils.run(FileUtils.path(path), service, "--stateless-rpc", "--advertise-refs", ".");
        return refs;
    }

    public static byte[] serviceCommand(String catalog, String service, byte[] requestBody) {
        return CommandUtils.runByOutputStream(requestBody, FileUtils.path(catalog),service, "--stateless-rpc");
    }
    public static String sendHead(String catalog) {
        return StringUtils.byteToString(
                FileUtils.getFileContent(catalog + "/HEAD"));
    }
    public static byte[] packetWrite(String data) {
        int length = data.length();
        if (length<2) {
            throw new DataLengthTooShortException();
        }
        StringBuilder ls = new StringBuilder(Integer.toHexString(length + 4).substring(2));

        int len = ls.length();
        int lenb = 4 - len % 4;
        byte[] prefix = new byte[lenb];
        for (int i = 0; i < lenb; i++) {
            prefix[i] = 0;
        }
        System.out.println(length + 4);
        return ByteUtils.merge(prefix, ls.toString().getBytes(), data.getBytes());
    }
    public static void main(String args[]) {
        byte[] data = packetWrite("# service=git-receive-pack\n");
        System.out.println(new String(data, Charset.forName("utf-8")));
    }
}
class DataLengthTooShortException extends RuntimeException {}