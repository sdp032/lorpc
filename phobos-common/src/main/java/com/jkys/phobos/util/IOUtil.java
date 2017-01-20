package com.jkys.phobos.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lo on 1/20/17.
 */
public class IOUtil {
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[10240];
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}
