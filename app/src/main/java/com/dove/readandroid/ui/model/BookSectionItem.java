package com.dove.readandroid.ui.model;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Created by Chu on 2017/5/28.
 */

public class BookSectionItem implements Serializable {
    /**
     * "ChapterIndex": 2181,
     * "ChapterName": "2181.第2181章 终极神话"
     */

    public String title;

    public String chapterUrl;

    public BookSectionItem(@Nullable String title, @Nullable String chapterUrl) {
        this.title=title;
        this.chapterUrl=chapterUrl;
    }
}
