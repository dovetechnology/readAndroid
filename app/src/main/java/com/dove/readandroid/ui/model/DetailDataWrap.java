package com.dove.readandroid.ui.model;

import java.io.Serializable;
import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/1/14 16:20
 * ===============================
 */
public class DetailDataWrap implements Serializable {
    public List<Book> top;
    public List<Book> hot;
    public List<Book> update;

}
