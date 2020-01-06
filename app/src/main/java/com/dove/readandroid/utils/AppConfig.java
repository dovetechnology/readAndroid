package com.dove.readandroid.utils;

import com.appbaselib.utils.PreferenceUtils;
import com.dove.readandroid.ui.App;
import com.dove.readandroid.ui.common.Constants;

import static com.dove.readandroid.ui.common.Constants.K_NIGHT_MODE;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/1/2 18:17
 * ===============================
 */
public class AppConfig {
    public static boolean isNightMode() {
        return PreferenceUtils.getPrefBoolean(App.instance, K_NIGHT_MODE, false);
    }

    public static void setNightMode(boolean isNightMode) {
        PreferenceUtils.setPrefBoolean(App.instance, K_NIGHT_MODE, isNightMode);
    }
}
