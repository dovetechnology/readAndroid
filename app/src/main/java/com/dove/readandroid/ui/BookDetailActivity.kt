package com.dove.readandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appbaselib.base.BaseMvcActivity
import com.dove.readandroid.R

class BookDetailActivity : BaseMvcActivity() {
    override fun initView(mSavedInstanceState: Bundle?) {
    }

    override fun getContentViewLayoutID(): Int {
        return  R.layout.activity_book_detail
    }


}
