package com.dove.readandroid.ui.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/27 14:48
 * ===============================
 */

@Entity
public class Book implements Serializable {


    /**
     * category : [dushi]
     * author : 鸿蒙树
     * name : 大国基建
     * stat : null
     * title : null
     * chapterUrl : null
     * coverImage : null
     * novelUrl : https://www.biqugg.com/xs/15433/
     * createTime : null
     * updateTime : null
     * description : null
     * novelList : null
     */
   // public String id;
    public String category;
    public String author;
    @NotNull
    @PrimaryKey()
    public String name;
    public String stat;
    public String title;
    public String chapterUrl;
    public String coverImage;
    public String novelUrl;
    public String createTime;
    public String updateTime;
    public String description;
    public String caseId;
    @Ignore
    public List<BookSectionItem> novelList;
    public String content;

    public boolean isRead = false;
    public int currentSetion = 0;//看到第几章 默认0 第一章

    public int isAddShlef = 0; //0 未加入 1加入书架
    @NotNull
    public String articleId;
    public String chapterId;
}
