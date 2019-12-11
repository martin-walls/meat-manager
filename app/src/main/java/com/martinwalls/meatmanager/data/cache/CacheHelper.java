package com.martinwalls.meatmanager.data.cache;

import android.content.Context;

import androidx.annotation.StringRes;

import java.io.*;
import java.net.URLEncoder;

/**
 * Helper class to handle reading and writing cache files. The names of cache files
 * should be stored as string resources.
 */
public class CacheHelper {

    /**
     * This shouldn't be instantiated.
     */
    private CacheHelper() {}

    /**
     * Saves the string {@code value} to cache in a file named {@code key}
     *
     * @param key   the string resource with the name of the cache file to write to
     * @param value the string to write
     */
    public static void save(Context context, @StringRes int key, String value) {
        try {
            File cache = getCacheFile(context, context.getString(key));

            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(cache));
            out.writeUTF(value);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the contents of the cache file named {@code key} as a string.
     *
     * @param key the string resource with the name of the cache file
     */
    public static String retrieve(Context context, @StringRes int key) {
        try {
            File cache = getCacheFile(context, context.getString(key));

            if (cache != null && cache.exists()) {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(cache));
                String value = in.readUTF();
                in.close();

                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Queries whether the specified cache file exists.
     *
     * @param key the string resource with the name of the cache file
     */
    public static boolean doesCacheExist(Context context, @StringRes int key) {
        File cache = getCacheFile(context, context.getString(key));
        return cache != null && cache.exists();
    }

    /**
     * Gets the path of the app's cache folder.
     */
    private static String getCacheDir(Context context) {
        return context.getCacheDir().getPath();
    }

    /**
     * Gets the cache file named {@code key} as a {@link File} object.
     */
    private static File getCacheFile(Context context, String key) {
        try {
            key = URLEncoder.encode(key, "UTF-8");
            return new File(getCacheDir(context) + "/" + key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
