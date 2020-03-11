package com.dove.readandroid.ui.common;

import com.dove.readandroid.BuildConfig;
import com.dove.readandroid.utils.PhoneUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/18 15:03
 * ===============================
 */
public class Constants {
    public static final String PATH_CACHE_DIR = PhoneUtil.getCachePath() + "/dove/";//图片缓存根目录
    public static final String IS_SHOW_AD = "is_show_ad";

    public static final String PATH_ROOT_DIR = PhoneUtil.getSdCardRootPath() + "/dove/";//图片缓存根目录
    public static final String PATH_DOWNLOAD_FILES_DIR = PATH_ROOT_DIR + "download/";//下载文件存放地址

    public static final String URL = BuildConfig.BASE_URL;
    public static final String USER = "user";
    public static final String TOKEN = "token";
    public static final String IMAGE="";
    public static final int TYPE_AD = 1;
    public static final int TYPE_JINGXUAN = 2;
    public static final int TYPE_RENQI = 3;
    public static final int TYPE_ZUIJING = 4;


    public static String IS_BLACK ="is_black";
    public static final String HISTORY = "history";
    public final static String K_NIGHT_MODE = "night_mode";
    public static String APPDATA="app_ata";
    public static final String SPEECH_SPEED="speed_preference";
    public static final String PITCH_SPEED="pitch_preference";
    public static final String VOLUME_SPEED="volume_preference";
    public static final String STREAM_SPEED="stream_preference";

    @Nullable
    public static final String AD="ad";
}
