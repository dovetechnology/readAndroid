package com.dove.readandroid.ui.model;

import java.io.Serializable;
import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/30 16:27
 * ===============================
 */
public class ChapterWrap implements Serializable {
    public  int chapters;
    public ChapterWrapWrap data;


    public static  class  ChapterWrapWrap implements Serializable
    {
        public List<BookSectionItem> list;

    }


}
