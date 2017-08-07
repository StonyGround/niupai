package com.xiuxiu.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/2/16.
 */
public class CacheUtils {

    private static SharedPreferences sp;

    public static boolean getBooleanFirst(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(XConstant.GUIDE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, true);// 默认返回false
    }

    public static void setBooleanFirst(Context context, String key, boolean values) {
        if (sp == null) {
            sp = context.getSharedPreferences(XConstant.GUIDE_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, values).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, false);// 默认返回false
    }

    public static void setBoolean(Context context, String key, boolean values) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, values).apply();
    }

    public static void setString(Context context, String key, String values) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, values).apply();
    }

    public static String getString(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, "");// 默认返回false
    }

    public static void setLong(Context context, String key, Long values) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putLong(key, values).apply();
    }

    public static Long getLong(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getLong(key, 0);// 默认返回false
    }

    public static void setInt(Context context, String key, int values) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, values).apply();
    }

    public static int getInt(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, 0);// 默认返回false
    }

    public static void delString(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).apply();
    }
}
