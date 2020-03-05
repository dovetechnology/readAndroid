package com.dove.readandroid.ui.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/1/7 17:59
 * ===============================
 */
@Dao
public interface ChapDao {

    @Query("select * from BookSectionItem where name= (:name)")
    List<BookSectionItem> findChap(String name);

    @Insert
    void add(BookSectionItem m);

    @Update
    void updata(BookSectionItem m);


    @Insert
    void addAll(List<BookSectionItem> chaps);
}
