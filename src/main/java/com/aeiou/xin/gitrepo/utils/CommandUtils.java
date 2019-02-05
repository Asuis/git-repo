package com.aeiou.xin.gitrepo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @author asuis
 * @version: CommandUtils.java 1/25/19:8:23 PM
 */
public class CommandUtils {

    private static final Logger logger = LoggerFactory.getLogger(CommandUtils.class);

    private static Runtime runtime = Runtime.getRuntime();
    public static byte[] runByOutputStream(byte[] out, File dir, String... args) {
        Process p = null;
        InputStream in = null;
        OutputStream outputStream = null;
        StringBuilder command = new StringBuilder("git");
        for (String arg : args) {
            command.append(" ").append(arg);
        }
        logger.info("exec command " + command);
        try {
            p = runtime.exec(command.toString(), null, dir);

            if (!p.waitFor(5, TimeUnit.SECONDS)) {
                return null ;
            }

            in = p.getInputStream();
            outputStream = p.getOutputStream();
            outputStream.write(out);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            return buffer;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (in!=null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream!=null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (p!=null) {
                p.destroy();
            }
        }
        return null;
    }
    public static byte[] run(File dir, String... args) {

        Process p = null;
        StringBuilder command = new StringBuilder("git");
        for (String arg : args) {
            command.append(" ").append(arg);
        }
        logger.info("exec command " + command);
        try {
            p = runtime.exec(command.toString(), null, dir);

            if (!p.waitFor(5, TimeUnit.SECONDS)) {
                return null ;
            }

            InputStream in = p.getInputStream();
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            return buffer;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (p!=null) {
                p.destroy();
            }
        }
        return null;
    }
    public static void main(String... args) {
        byte[] b = run(new File("/media/asuis/kit/workspace/git/git_server_test.git"), "--stateless-rpc", "--advertise-refs");
        String bb = new String(b, Charset.defaultCharset());
        System.out.println(bb);
    }
}
