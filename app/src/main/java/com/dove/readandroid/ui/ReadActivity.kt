package com.dove.readandroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.ext.toast
import com.appbaselib.utils.ScreenUtils
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.model.BookSectionContent
import com.dove.readandroid.ui.model.BookSectionItem
import com.dove.readandroid.utils.AppConfig
import com.dove.readandroid.utils.BrightnessUtils
import com.dove.readandroid.utils.SystemBarUtils
import com.dove.readandroid.view.page.ReadTheme
import com.dove.readandroid.view.page.ReaderSettingManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.leaf.library.StatusBarUtil
import com.safframework.ext.click
import com.zchu.reader.PageLoaderAdapter
import com.zchu.reader.PageView
import kotlinx.android.synthetic.main.activity_read.*
import kotlinx.android.synthetic.main.layout_read_bottom.*
import kotlinx.android.synthetic.main.title_bar.*
import kotlinx.android.synthetic.main.toolbar_read.*
import q.rorbin.badgeview.DisplayUtil

class ReadActivity : BaseMvcActivity() {

    lateinit var mbook: Book

    //适配5.0 以下手机可以正常显示vector图片


    private val K_EXTRA_BOOK_TB = "book_tb"


    lateinit var mTopInAnim: Animation
    lateinit var mTopOutAnim: Animation
    lateinit var mBottomInAnim: Animation
    lateinit var mBottomOutAnim: Animation

    private var mReadSettingDialog: BottomSheetDialog? = null

    private val canTouch = true
    private val adapter: PageLoaderAdapter? = null

    //控制屏幕常亮
    private var mWakeLock: PowerManager.WakeLock? = null
    private val isFullScreen = false

    private var isShowCollectionDialog = false

    lateinit var sectionAdapter: BookSectionAdapter
    lateinit var readAdapter: ReadAdapter


    @SuppressLint("InvalidWakeLockTag")
    override fun initView(mSavedInstanceState: Bundle?) {
        mbook = intent.getSerializableExtra("data") as Book

        //
        StatusBarUtil.setTransparentForWindow(this)
        ReaderSettingManager.init(this)
//        bindOnClickLister(
//            this,
//            readTvPreChapter,
//            readTvNextChapter,
//            readTvCategory,
//            readTvNightMode,
//            readTvSetting
//        )

        if (Build.VERSION.SDK_INT >= 19) {
            appbar.setPadding(0, ScreenUtils.getStatusHeight(this), 0, 0)
        }
        //半透明化StatusBar
        //隐藏StatusBar
        appbar.post(Runnable { hideSystemBar() })

        //初始化屏幕常亮类
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "keep bright")
        //设置当前Activity的Bright
        if (ReaderSettingManager.getInstance().isBrightnessAuto()) {
            BrightnessUtils.setUseSystemBrightness(this)
        } else {
            BrightnessUtils.setBrightness(this, ReaderSettingManager.getInstance().getBrightness())
        }

        pv_read.setTextSize(ReaderSettingManager.getInstance().getTextSize())
        if (AppConfig.isNightMode()) {
            ReaderSettingManager.getInstance()
                .setPageBackground(ReadTheme.NIGHT.getPageBackground())
            ReaderSettingManager.getInstance().setTextColor(ReadTheme.NIGHT.getTextColor())
        }
        pv_read.setTextColor(ReaderSettingManager.getInstance().getTextColor())
        pv_read.setPageBackground(ReaderSettingManager.getInstance().getPageBackground())

        pv_read.setTouchListener(object : PageView.TouchListener {
            override fun center() {
                toggleMenu(true)
            }

            override fun cancel() {

            }
        })
        pv_read.setOnTouchListener(View.OnTouchListener { v, event ->
            if (appbar.getVisibility() === View.VISIBLE) {
                hideReadMenu()
                return@OnTouchListener true
            }
            false
        })
        //loaddata
        //加载目录
        var titles = arrayListOf<BookSectionItem>()
        mbook.novelList.forEach {
            titles.add(BookSectionItem(it.title, it.chapterUrl))
        }
        sectionAdapter = BookSectionAdapter(R.layout.list_item_book_section, titles)
        sectionAdapter.setTextColor(pv_read.textColor)
        read_rv_section.setLayoutManager(LinearLayoutManager(this))
        read_rv_section.adapter = sectionAdapter

        sectionAdapter.setOnItemClickListener { adapter, view, position ->

            read_drawer.closeDrawers()
            getContentByChap(titles.get(position).chapterUrl)
        }
        pv_read.setOnThemeChangeListener(PageView.OnThemeChangeListener { textColor, backgroundColor, textSize ->
            read_rv_section.setBackgroundColor(backgroundColor)
            if (sectionAdapter != null) {
                sectionAdapter.setTextColor(textColor)
            }
        })

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in)
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out)
        //退出的速度要快
        mTopOutAnim.setDuration(200)
        mBottomOutAnim.setDuration(200)

        readAdapter = ReadAdapter()
        pv_read.setAdapter(readAdapter)
        //点击事件

        read_tv_category.click {
            read_drawer.openDrawer(read_side)
        }
        read_tv_setting.click {
            openReadSetting(this)
        }
        tv_add.click {

            http().mApiService.addShujia(mbook.name, mbook.author, mbook.title)
                .get3 (isShowDialog = true){
                    toast("已加入书架")
                }
        }
    }

    fun getContentByChap(url: String) {
        http().mApiService.openChap(mbook.novelUrl, url)
            .get3 {
                var content = it?.data?.content?.replace("<br>", "")

                content = content?.replace("&nbsp;", "")

                readAdapter.addData(1, BookSectionContent(1, it?.data?.title, content))
                pv_read.openSection(1, 1)
            }
    }
    private fun openReadSetting(context: Context) {
        if (mReadSettingDialog == null) {
            mReadSettingDialog = ReaderSettingDialog(context, pv_read,lin)
        }
        mReadSettingDialog?.show()
    }

    private fun hideSystemBar() {
        //隐藏
        SystemBarUtils.hideStableStatusBar(this)
        if (isFullScreen) {
            SystemBarUtils.hideStableNavBar(this)
        }
    }

    //初始化菜单动画
    private fun initMenuAnim() {

        mBottomInAnim?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                pv_read.setCanTouch(false)
            }

            override fun onAnimationEnd(animation: Animation) {

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        mBottomOutAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                pv_read.setCanTouch(true)

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private fun toggleMenu(hideStatusBar: Boolean) {
        initMenuAnim()

        if (appbar.getVisibility() === VISIBLE) {
            //关闭
            appbar.startAnimation(mTopOutAnim)
            read_bottom.startAnimation(mBottomOutAnim)
            appbar.setVisibility(GONE)
            read_bottom.setVisibility(GONE)

            if (hideStatusBar) {
                hideSystemBar()
            }

        } else {
            appbar.setVisibility(VISIBLE)
            read_bottom.setVisibility(VISIBLE)
            appbar.startAnimation(mTopInAnim)
            read_bottom.startAnimation(mBottomInAnim)
            val isNight = ReadTheme.getReadTheme(
                pv_read.getPageBackground(),
                pv_read.getTextColor()
            ) === ReadTheme.NIGHT
            read_tv_night_mode.setSelected(isNight)
            read_tv_night_mode.setText(
                if (isNight) getString(R.string.read_daytime) else getString(
                    R.string.read_night
                )
            )
            showSystemBar()
        }
    }

    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private fun hideReadMenu(): Boolean {
        hideSystemBar()
        if (appbar.getVisibility() === VISIBLE) {
            toggleMenu(true)
            return true
        }
        return false
    }

    private fun showSystemBar() {
        //显示
        SystemBarUtils.showUnStableStatusBar(this)
        if (isFullScreen) {
            SystemBarUtils.showUnStableNavBar(this)
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_read
    }


}
