package com.dove.readandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appbaselib.base.BaseMvcActivity
import com.dove.readandroid.R
import com.dove.readandroid.ui.model.AdData
import com.safframework.ext.click
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import kotlinx.android.synthetic.main.activity_play_video.*

class PlayVideoActivity : BaseMvcActivity() {
    lateinit var ad: AdData
    override fun initView(mSavedInstanceState: Bundle?) {
        ad = (intent.getSerializableExtra("data") as AdData)
        ivback.click {
            finish()
        }
        GSYVideoManager.instance().setPlayerInitSuccessListener { player, model ->
            GSYVideoManager.instance().isNeedMute = false            //设置静音
        }
        GSYVideoOptionBuilder().apply {
            setUrl(ad?.videoUrl)
                .setCacheWithPlay(true)
                .build(vv)
        }
        vv.startPlayLogic()
        vv.isEnabled = false
        content.click {
            OpenTypeHandler(ad, mContext).handle()
            finish()
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_play_video
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

}
