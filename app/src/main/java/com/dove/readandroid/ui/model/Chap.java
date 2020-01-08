package com.dove.readandroid.ui.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/27 14:48
 * ===============================
 */
@Entity(foreignKeys = @ForeignKey(entity = Book.class, parentColumns = "id", childColumns = "book_id",onDelete = CASCADE)
        , indices = @Index(value = {"book_name"}, unique = true))
public class Chap implements Serializable {


    /**
     * category : [都市]
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

    public String author;
    @ColumnInfo(name = "book_name")
    public String name;
    public String title;
    public String chapterUrl;
    public String coverImage;
    public String novelUrl;
    public String createTime;
    public String updateTime;
    public String description;
    public String content;
    @PrimaryKey(autoGenerate = true)
    public int id;
    public  int book_id;

}
