package com.dove.readandroid.ui.shucheng;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.dove.readandroid.ui.common.Constants;
import com.dove.readandroid.ui.model.Book;

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/3/10 13:58
 * ===============================
 */
public class JingxuanData extends Book implements MultiItemEntity {
    @Override
    public int getItemType() {
        return Constants.TYPE_JINGXUAN;
    }
}
