package com.dove.readandroid.ui.model;

import java.io.Serializable;
import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/27 14:48
 * ===============================
 */
public class Book implements Serializable {


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

    public String category;
    public String author;
    public String name;
    public String stat;
    public String title;
    public String chapterUrl;
    public String coverImage;
    public String novelUrl;
    public String createTime;
    public String updateTime;
    public String description;
    public List<Book> novelList;
}
