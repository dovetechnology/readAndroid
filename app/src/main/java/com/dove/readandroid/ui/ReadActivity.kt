package com.dove.readandroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.ext.toast
import com.appbaselib.utils.ScreenUtils
import com.dove.readandroid.R
import com.dove.readandroid.event.ShujiaEvent
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.leaf.library.StatusBarUtil
import com.safframework.ext.click
import com.safframework.ext.postDelayed
import com.zchu.reader.OnPageChangeListener
import com.zchu.reader.PageView
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.activity_read.*
import kotlinx.android.synthetic.main.layout_read_bottom.*
import kotlinx.android.synthetic.main.toolbar_read.*
import kotlinx.android.synthetic.main.toolbar_read.tv_add
import org.greenrobot.eventbus.EventBus

class ReadActivity : BaseMvcActivity() {

    lateinit var mbook: Book
    private val K_EXTRA_BOOK_TB = "book_tb"
    lateinit var mTopInAnim: Animation
    lateinit var mTopOutAnim: Animation
    lateinit var mBottomInAnim: Animation
    lateinit var mBottomOutAnim: Animation

    private var mReadSettingDialog: BottomSheetDialog? = null

    //控制屏幕常亮
    private var mWakeLock: PowerManager.WakeLock? = null
    private val isFullScreen = false

    private var isShowCollectionDialog = false

    lateinit var sectionAdapter: BookSectionAdapter
    lateinit var readAdapter: ReadAdapter
    lateinit var mSectionItem: BookSectionItem //当前章节


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
        //初始化各种背景图
        pv_read.setTextColor(ReaderSettingManager.getInstance().getTextColor())
        pv_read.setPageBackground(ReaderSettingManager.getInstance().getPageBackground())
        lin.setBackgroundColor(ReaderSettingManager.getInstance().getPageBackground())
        read_bottom.setBackgroundColor(ReaderSettingManager.getInstance().getPageBackground())
        StatusBarUtil.setColor(this, ReaderSettingManager.getInstance().getPageBackground())

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

        sectionAdapter = BookSectionAdapter(R.layout.list_item_book_section, mbook.novelList)
        sectionAdapter.setTextColor(pv_read.textColor)
        read_rv_section.setLayoutManager(LinearLayoutManager(this))
        read_rv_section.adapter = sectionAdapter


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
                .get3(isShowDialog = true) {
                    mbook.isAddShlef = 1
                    App.instance.db.getBookDao().update(mbook)
                    EventBus.getDefault().post(ShujiaEvent())
                    toast("已加入书架")
                }
        }

        pv_read.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageChange(pos: Int) {
                print("页面改变$pos")
                mSectionItem.currentPage = pos
                //在这里判断 需不需要加载下一章 上一章
                if (!readAdapter.hasNextSection(pv_read.chapPosition)) {
                    //加载下一章
                    var p = mbook.novelList.indexOf(mSectionItem) + 1
                    if (p < mbook.novelList.size)

                        getContentByChap(mbook.novelList.get(p), false) {
                            readAdapter.addData(
                                p,
                                BookSectionContent(
                                    p,
                                    mbook.novelList.get(p).title,
                                    mbook.novelList.get(p).content
                                )
                            )
                        }
                }
                if (!readAdapter.hasPreviousSection(pv_read.chapPosition)) {
                    //加载上一章
                    var p = mbook.novelList.indexOf(mSectionItem) - 1
                    if (p >= 0)

                        getContentByChap(mbook.novelList.get(p), false) {
                            readAdapter.addData(
                                p,
                                BookSectionContent(
                                    p,
                                    mbook.novelList.get(p).title,
                                    mbook.novelList.get(p).content
                                )
                            )
                        }
                }


            }

            override fun onChapterChange(pos: Int) {
                print("章节改变$pos")
                mSectionItem = mbook.novelList.get(pos)
                mbook.currentSetion = pos
            }

            override fun onPageCountChange(count: Int) {
                print("页面页数改变$count")

            }
        })

        //开始阅读
        if (mbook.currentSetion != 0) {
            //渡过
        } else {
            //没读过
        }
        startRead(mbook.currentSetion)

        //选中章节
        sectionAdapter.setOnItemClickListener { adapter, view, position ->
            read_drawer.closeDrawers()
            mSectionItem = mbook.novelList.get(position)
            postDelayed(time = 300) {
                startRead(position)
            }
        }

        read_tv_night_mode.click {
            val nightModeSelected = !read_tv_night_mode.isSelected()
            toggleNightMode(nightModeSelected)
            ReaderSettingManager.getInstance().setNightMode(nightModeSelected)
            AppConfig.setNightMode(nightModeSelected)
            hideReadMenu()
            hideSystemBar()
        }
        //上一章
        read_tv_pre_chapter.click {
        }
        read_tv_next_chapter.click {

        }
        iv_close_ac.click {
            finish()
        }
    }

    private fun toggleNightMode(isOpen: Boolean) {
        if (isOpen) {
            read_tv_night_mode.setText(getString(R.string.read_daytime))
            read_tv_night_mode.setSelected(true)
            pv_read.setPageBackground(ReadTheme.NIGHT.pageBackground)
            pv_read.setTextColor(ReadTheme.NIGHT.textColor)
            pv_read.refreshPage()
            ReaderSettingManager.getInstance().pageBackground = pv_read.getPageBackground()
            ReaderSettingManager.getInstance().textColor = pv_read.getTextColor()


        } else {
            read_tv_night_mode.setText(getString(R.string.read_night))
            read_tv_night_mode.setSelected(false)
            pv_read.setPageBackground(ReadTheme.DEFAULT.pageBackground)
            pv_read.setTextColor(ReadTheme.DEFAULT.textColor)
            pv_read.refreshPage()
            ReaderSettingManager.getInstance().pageBackground = pv_read.getPageBackground()
            ReaderSettingManager.getInstance().textColor = pv_read.getTextColor()
        }
        //还原面板颜色
        lin.setBackgroundColor(ReaderSettingManager.getInstance().getPageBackground())
        read_bottom.setBackgroundColor(ReaderSettingManager.getInstance().getPageBackground())
        StatusBarUtil.setColor(this, ReaderSettingManager.getInstance().getPageBackground())
    }

    fun startRead(p: Int) {

        mSectionItem = mbook.novelList.get(p)

        if (mSectionItem.content.isNullOrEmpty()) {
            //从网络获取
            getContentByChap(mSectionItem) {
                readAdapter.addData(
                    p,
                    BookSectionContent(p, mSectionItem.title, mSectionItem.content)
                )
                pv_read.openSection(p, 0) //从网络获取的就从第一页读
            }

        } else {
            //本地
            readAdapter.addData(
                p,
                BookSectionContent(p, mSectionItem.title, mSectionItem.content)
            )
            pv_read.openSection(p, mSectionItem.currentPage)//从本地获取的就接着读
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        //保存当前进度
        App.instance.db.getBookDao().update(mbook)
        App.instance.db.getChapDao().updata(mSectionItem) //记录当前章节进度
    }

    fun getContentByChap(
        bookSectionItem: BookSectionItem,
        isShowtitle: Boolean = true,
        next: () -> Unit
    ) {

        http().mApiService.openChap(mbook.novelUrl, bookSectionItem.chapterUrl)
            .get3(isShowDialog = isShowtitle, title = "", message = "加载章节中……") {
                var content = it?.data?.content?.replace("<br>", "")
                content = content?.replace("&nbsp;", "")
                bookSectionItem.content = content//改变了mbook书里面的内容
                App.instance.db.getChapDao().updata(bookSectionItem) //保存到数据库
                next()
            }


    }

    private fun openReadSetting(context: Context) {
        if (mReadSettingDialog == null) {
            mReadSettingDialog = ReaderSettingDialog(context, this, pv_read, lin, read_bottom)
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
