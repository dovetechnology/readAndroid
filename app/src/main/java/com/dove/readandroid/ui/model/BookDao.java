package com.dove.readandroid.ui.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
    void addAll(List<Book> m);

    @Insert
    void add(Book m);

    @Query("select * from book where name=:name")
    Book find(String name);
    @Query("select * from book where isAddShlef=1")
    List<Book> shujia( );


    @Update
    void update(Book m);

    @Query("DELETE FROM book")
    void deleteAll();
}
