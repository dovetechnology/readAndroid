package com.dove.readandroid.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.StyleRes
import androidx.cardview.widget.CardView
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import com.appbaselib.common.load
import com.dove.readandroid.ui.OpenTypeHandler
import com.dove.readandroid.ui.model.AdData
import com.gcssloop.widget.RCRelativeLayout
import com.safframework.ext.click
import android.widget.*
import com.appbaselib.view.RatioFramlayout
import com.dove.readandroid.ui.PlayVideoActivity
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.video.GSYADVideoPlayer
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer


class AdView : FrameLayout {

    lateinit var rclayout: FrameLayout
    var imageView: ImageView? = null
    var videoView: SimpleVideoView? = null
    lateinit var close: View

    constructor(@NonNull context: Context) : super(context) {}

    @JvmOverloads
    constructor(
        @NonNull context: Context, @Nullable attrs: AttributeSet,
        @AttrRes defStyleAttr: Int = 0, @StyleRes defStyleRes: Int = 0
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    init {
        initView()
    }

    lateinit var ad: AdData
    lateinit var view: View

    fun setData(ad: AdData) {
        this.ad = ad
        setValue()
    }

    fun setValue() {

        rclayout = view.findViewById<View>(com.dove.readandroid.R.id.relayout) as RatioFramlayout

        if (!ad.imgUrl.isNullOrEmpty()) {
            imageView = ImageView(context).apply {
                this.scaleType = ImageView.ScaleType.FIT_XY
            }
            rclayout.addView(imageView, RelativeLayout.LayoutParams(-1, -1))
            imageView?.load(ad.imgUrl)
            imageView?.click {
                OpenTypeHandler(ad, context).handle()
            }
        }
        if (!ad.videoUrl.isNullOrEmpty()) {
            GSYVideoManager.instance().setPlayerInitSuccessListener { player, model ->
                GSYVideoManager.instance().isNeedMute = true            //设置静音
            }

            videoView = SimpleVideoView(context)
            rclayout.addView(videoView, RelativeLayout.LayoutParams(-1, -1))

            var clicklayout = FrameLayout(context)
            rclayout.addView(clicklayout, FrameLayout.LayoutParams(-1, -1))

            GSYVideoOptionBuilder().apply {
                setUrl(ad.videoUrl)
                    .setCacheWithPlay(true)
                    .build(videoView)
            }
            videoView?.isEnabled=false
            videoView?.startPlayLogic()
            clicklayout?.click {
                context.startActivity(Intent(context, PlayVideoActivity::class.java).apply {
                    putExtra("data", ad)
                })
            }
        }

    }

    fun initView() {
        view = View.inflate(context, com.dove.readandroid.R.layout.view_ad, this)
        close = view.findViewById<ImageView>(com.dove.readandroid.R.id.iv_close)
        close.click {
            this.visibility = View.GONE
        }

        // addView(view)
    }

}
