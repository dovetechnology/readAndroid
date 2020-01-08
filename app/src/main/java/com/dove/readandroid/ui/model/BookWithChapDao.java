package com.dove.readandroid.ui.model;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/1/8 14:16
 * ===============================
 */
@Dao
public interface BookWithChapDao {

    @Transaction
    @Query("select * from book")
    List<BookWithChap> getAllBook();
}
