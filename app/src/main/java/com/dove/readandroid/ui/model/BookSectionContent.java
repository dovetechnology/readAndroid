package com.dove.readandroid.ui.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Chu on 2017/5/28.
 */

public class BookSectionContent implements Serializable {

    public BookSectionContent(int sectionIndex, String sectionName, String content) {
        this.sectionIndex = sectionIndex;
        this.sectionName = sectionName;
        this.content = content;
    }

    /**
     * ChapterIndex : 1
     * ChapterName : 1.第1章 惨遭遗弃
     * ChapterContent : ..
     * CreateDateTime : 2017-03-28T23:30:43.01
     */

    public int sectionIndex;

    public String sectionName;

    public String content;

    public String createDateTime;

    public String sectionId;



}
