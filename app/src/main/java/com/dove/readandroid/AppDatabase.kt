package com.dove.readandroid

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dove.readandroid.ui.model.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/11/21 14:43
 * ===============================
 */
@Database(entities = [Book::class, BookSectionItem::class,BookShelf::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getChapDao(): ChapDao
    abstract fun getBookDao(): BookDao
    abstract fun getBookWithChapDao(): BookWithChapDao
    abstract  fun getBookShelfDao():BookShelfDao

}