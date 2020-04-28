package com.fomin.tvradio.utils;

import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            int total = inputStream.read(bytes, 0, bytes.length);
            System.out.println("Read " + total + " bytes");
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }
}
