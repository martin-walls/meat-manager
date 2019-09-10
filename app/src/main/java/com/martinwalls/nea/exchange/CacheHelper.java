package com.martinwalls.nea.exchange;

import android.content.Context;

import java.io.*;
import java.net.URLEncoder;

public class CacheHelper {

    private CacheHelper() {}

    private static String getCacheDir(Context context) {
        return context.getCacheDir().getPath();
    }

    private static File getCacheFile(Context context, String key) {
        try {
            key = URLEncoder.encode(key, "UTF-8");
            return new File(getCacheDir(context) + "/" + key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void save(Context context, String key, String value) {
        try {
            File cache = getCacheFile(context, key);

            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(cache));
            out.writeUTF(value);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String retrieve(Context context, String key) {
        try {
            File cache = getCacheFile(context, key);

            if (cache.exists()) {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(cache));
                String value = in.readUTF();
                in.close();

                return value;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean doesCacheExist(Context context, String key) {
        File cache = getCacheFile(context, key);
        if (cache == null) {
            return false;
        }
        return cache.exists();
    }
}
