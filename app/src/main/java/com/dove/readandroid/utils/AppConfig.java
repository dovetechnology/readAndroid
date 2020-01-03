package com.dove.readandroid.utils;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/1/2 18:17
 * ===============================
 */
public class AppConfig {
    public static boolean isNightMode() {
        return getPreferences().getBoolean(K_NIGHT_MODE, false);
    }

    public static void setNightMode(boolean isNightMode) {
        getPreferences()
                .edit()
                .putBoolean(K_NIGHT_MODE, isNightMode)
                .apply();
    }
}
