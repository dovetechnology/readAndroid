package com.dove.readandroid.ui.model;

import java.io.Serializable;
import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/27 14:30
 * ===============================
 */
public class HomeData implements Serializable {
    public List<Book> update;
    public List<Book> hot;
    public List<Book> newin;
    public List<Book> besthot;

}
