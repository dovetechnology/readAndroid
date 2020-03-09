package com.dove.readandroid.ui.model;

import androidx.room.Delete;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/3/9 15:19
 * ===============================
 */
public interface BookShelfDao {
    @Delete
    void deteleAll();
}
