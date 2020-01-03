package com.dove.readandroid.view.page;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dove.readandroid.R;
import com.dove.readandroid.ui.App;
/**
 * author : zchu
 * date   : 2017/8/22
 * desc   :
 */

public enum ReadTheme {

    WHITE(App.instance, R.color.read_theme_white_page_background, R.color.read_theme_white_text),
    AMBER(App.instance, R.color.read_theme_amber_page_background, R.color.read_theme_amber_text),
    GREEN(App.instance, R.color.read_theme_green_page_background, R.color.read_theme_green_text),
    BROWN(App.instance, R.color.read_theme_brown_page_background, R.color.read_theme_brown_text),
    BLACK(App.instance, R.color.read_theme_black_page_background, R.color.read_theme_black_text),
    NIGHT(App.instance, R.color.read_theme_night_page_background, R.color.read_theme_night_text),
    DEFAULT(App.instance, R.color.read_theme_default_page_background, R.color.read_theme_default_text);


    ReadTheme(Context context, @ColorRes int pageBackgroundRes, @ColorRes int textColorRes) {
        this.pageBackground = ContextCompat.getColor(context, pageBackgroundRes);
        this.textColor = ContextCompat.getColor(context, textColorRes);
    }

    @Nullable
    public static ReadTheme getReadTheme(int pageBackground, int textColor) {
        for (ReadTheme readTheme : ReadTheme.values()) {
            if (readTheme.pageBackground == pageBackground && readTheme.textColor == textColor) {
                return readTheme;
            }
        }
        return null;
    }

    private int pageBackground;
    private int textColor;

    public int getPageBackground() {
        return pageBackground;
    }

    public void setPageBackground(int pageBackground) {
        this.pageBackground = pageBackground;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
