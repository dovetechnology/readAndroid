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
    List<Book> shujia();

    @Query("select * from book where caseId is not null or trim(caseId)!=''")
    List<Book> shujiat();

    @Query("update book set isAddShlef=1 where name=:s")
    void addShelf(String s);



    @Update
    void update(Book m);

    @Query("DELETE FROM book")
    void deleteAll();

    @Query("select * from bookshelf order by joinTime desc")
    List<BookShelf> getShujia();

    @Insert
    void addShujia(BookShelf m);

    @Insert
    void addShujias(List<BookShelf> mlist);

    @Query("DELETE from bookshelf where name=:name")
    void remove(String name);

    @Query("select * from bookshelf where name=:name")
    BookShelf findShelf(String name);

}
