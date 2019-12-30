package com.dove.readandroid.ui.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/23 17:05
 * ===============================
 */
public class Top implements Serializable {
    public List<Top> monthList;
    public String novelName;
    public String topName;
    public String novelUrl;
    public List<Top> totalList ;
    public String updateTime;
    public  List<Top> weekList;
    public String name;
}
