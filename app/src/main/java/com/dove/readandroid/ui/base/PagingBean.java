package com.dove.readandroid.ui.base;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ===============================
 * 描    述：分页查询数据模型
 * 作    者：pjw
 * 创建日期：2019/4/29 13:54
 * ===============================
 */
public class PagingBean<D> implements Serializable {

    private int count;//(integer, optional): 总记录数 ,
    private ArrayList<D> list;// (Array[opus], optional): 分页记录 ,
    private int totalPage;//(integer, optional): 总页数

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<D> getList() {
        return list;
    }

    public void setList(ArrayList<D> list) {
        this.list = list;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
