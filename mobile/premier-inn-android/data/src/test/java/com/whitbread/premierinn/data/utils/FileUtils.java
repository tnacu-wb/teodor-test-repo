package com.whitbread.premierinn.data.utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * todo replace with extension function
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getCanonicalName();

    private FileUtils() {
        throw new AssertionError("No instances allowed.");
    }

    public static String loadFileFromResource(String filename) {
        InputStream is = ClassLoader.getSystemResourceAsStream(filename);
        return convertInputStreamToString(is);
    }

    public static String loadFileFromAsset(Context context, String filename) {
        InputStream is = null;
        try {
            is = context.getAssets().open(filename);
        } catch (IOException e) {
            Log.w(TAG, "loadFileFromAsset", e);
            return null;
        }
        return convertInputStreamToString(is);
    }

    public static String convertInputStreamToString(InputStream inputStream) {
        String result;
        try {
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();

            result = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.w(TAG, "convertInputStreamToString()", e);
            return null;
        }
        return result;
    }
}
