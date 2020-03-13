package com.dove.readandroid.ui.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/3/9 15:19
 * ===============================
 */
@Dao
public interface BookShelfDao {
    @Query("Delete from bookshelf")
    void deteleAll();

    //删除某一项
    @Delete
    void delete(BookShelf... users);

    @Query("select * from bookshelf order by joinTime desc")
    List<BookShelf> getShujia();
}
