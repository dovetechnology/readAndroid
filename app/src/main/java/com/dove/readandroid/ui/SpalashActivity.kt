package com.dove.imuguang.ui.start

import android.os.Handler
import android.view.View
import android.widget.MediaController
import com.appbaselib.base.BaseMvcActivity
import android.content.Intent
import android.os.Bundle
import com.appbaselib.base.Navigator
import com.dove.readandroid.R
import com.dove.readandroid.ui.shujia.MainActivity

class SpalashActivity : BaseMvcActivity() {
    override fun initView(mSavedInstanceState: Bundle?) {

        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }

        Navigator(supportFragmentManager, R.id.content).showFragment(SpalashFragment({

            start(MainActivity::class.java)
            finish()

        }))

    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_spalash
    }

    override fun getLoadingTargetView(): View? {
        return null
    }

    override fun onBackPressed() {

    }

}
