package com.dove.readandroid.ui.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/1/7 17:47
 * ===============================
 */
@Dao
public interface BookDao {

    @Query("SELECT * FROM Book")
    List<Book> getAllBook();

    @Insert
    void add(Book m);
}
