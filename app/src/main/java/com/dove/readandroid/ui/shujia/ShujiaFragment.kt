package com.dove.readandroid.ui.shujia

import android.view.View
import com.appbaselib.base.BaseMvcFragment
import com.dove.readandroid.R
import com.safframework.ext.click
import kotlinx.android.synthetic.main.fragment_huodong.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class ShujiaFragment :BaseMvcFragment(){


    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_huodong
    }

    override fun initView() {
        lin.click {
            start(LoginActivity::class.java)
        }
    }

}