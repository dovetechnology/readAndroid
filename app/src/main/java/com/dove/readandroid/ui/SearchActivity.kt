package com.dove.readandroid.ui

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView


import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.base.Navigator
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.R
import com.dove.readandroid.ui.common.Constants
import com.safframework.ext.click
import com.safframework.ext.hideKeyboard
import com.safframework.ext.postDelayed
import com.safframework.ext.textChange
import kotlinx.android.synthetic.main.activity_search.*

/**
 * ===============================
 * 描    述：搜索
 * 作    者：pjw
 * 创建日期：2019/3/26 10:21
 * ===============================
 */
class SearchActivity : BaseMvcActivity() {

    var isVideo = false

    var mgtagframgent: SearchTagFragment = SearchTagFragment.getInstance() {
        search(it)
    }
    lateinit var resultFramgent: BookFragment
    lateinit var mNavigator: Navigator
    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_search

    }

    override fun getLoadingTargetView(): View? {
        return null
    }

    override fun initView(mSavedInstanceState: Bundle?) {

        isVideo = intent.getBooleanExtra("data", false)
        tvCancel.click {
            finish()
        }
        resultFramgent = BookFragment()
        etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //搜索
                var history = PreferenceUtils.getPrefString(mContext, Constants.HISTORY, "")
                history = history + etSearch.text.toString() + ","
                PreferenceUtils.setPrefString(mContext, Constants.HISTORY, history)

                search(etSearch.text.toString())

                return@OnEditorActionListener true
            }
            false
        })
        etSearch.textChange {
            if (TextUtils.isEmpty(it))
                mNavigator.showFragment(mgtagframgent)

        }

        mNavigator = Navigator(supportFragmentManager, R.id.content)
        mNavigator.showFragment(resultFramgent)
        mNavigator.showFragment(mgtagframgent)

    }

    private fun search(string: String) {
        etSearch.hideKeyboard()
        if (!TextUtils.isEmpty(string)) {

            mNavigator.showFragment(resultFramgent)
            resultFramgent.search(string)
        }
    }

}
