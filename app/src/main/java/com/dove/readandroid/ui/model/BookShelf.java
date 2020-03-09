package com.dove.readandroid.ui.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/3/6 11:15
 * ===============================
 */
@Entity
public class BookShelf implements Serializable {

    /**
     * articleId : 0
     * author : string
     * caseId : 0
     * chapterId : 0
     * img : string
     * joindate : 0
     * name : string
     * title : string
     * updateTime : string
     * userId : 0
     */

    public String articleId;
    public String author;
    @NotNull
    @PrimaryKey
    public String caseId;
    public String chapterId;
    public String img;
    public String joindate;
    public String name;
    public String title;
    public String updateTime;
    public String userId;
    public long joinTime;

}
