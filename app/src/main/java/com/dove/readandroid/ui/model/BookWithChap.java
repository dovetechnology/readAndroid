package com.dove.readandroid.ui.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/1/8 15:07
 * ===============================
 */
public class BookWithChap implements Serializable {
    @Embedded
    public Book book;
    @Relation(parentColumn = "name", entityColumn = "book_name")
    public List<BookSectionItem> novelList;
}
