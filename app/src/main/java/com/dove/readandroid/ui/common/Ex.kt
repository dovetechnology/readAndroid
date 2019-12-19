package com.dove.readandroid.ui.common

import com.dove.readandroid.ui.RetrofitHelper

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/18 15:56
 * ===============================
 */
//-------------------------------------------------------------------------------------------------------------
//简化网络库的调用
fun Any.http(): RetrofitHelper {
    return RetrofitHelper.getInstance()
}