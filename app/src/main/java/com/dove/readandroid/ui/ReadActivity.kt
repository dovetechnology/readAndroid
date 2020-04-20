package com.dove.readandroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.ext.toast
import com.appbaselib.network.RxHttpUtil
import com.appbaselib.utils.LogUtils
import com.appbaselib.utils.ScreenUtils
import com.dove.readandroid.R
import com.dove.readandroid.event.ShujiaEvent
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.get4
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
import com.safframework.ext.isVisible
import com.safframework.ext.postDelayed
import com.zchu.reader.OnPageChangeListener
import com.zchu.reader.PageView
import kotlinx.android.synthetic.main.activity_read.*
import kotlinx.android.synthetic.main.layout_read_bottom.*
import kotlinx.android.synthetic.main.toolbar_read.*
import kotlinx.android.synthetic.main.toolbar_read.tv_add
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import java.util.*

class ReadActivity : BaseMvcActivity() {

    lateinit var mbook: Book
    private val K_EXTRA_BOOK_TB = "book_tb"
    lateinit var mTopInAnim: Animation
    lateinit var mTopOutAnim: Animation
    lateinit var mBottomInAnim: Animation
    lateinit var mBottomOutAnim: Animation

    private var mReadSettingDialog: BottomSheetDialog? = null
    private var mSpeakDialog: SpeakDialog? = null

    //控制屏幕常亮
    private var mWakeLock: PowerManager.WakeLock? = null
    lateinit var sectionAdapter: BookSectionAdapter
    lateinit var readAdapter: ReadAdapter
    lateinit var mSectionItem: BookSectionItem //当前章节


    @SuppressLint("InvalidWakeLockTag")
    override fun initView(mSavedInstanceState: Bundle?) {
        mbook = intent.getSerializableExtra("data") as Book
        mSectionItem = mbook.novelList.get(mbook.currentSetion)
        //
        // StatusBarUtil.setTransparentForWindow(this)

        initData()
    }

    fun initData() {
        ReaderSettingManager.init(this)
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
        // read_bottom.setBackgroundColor(ReaderSettingManager.getInstance().getPageBackground())
        read_bottom.setBackgroundColor(ReaderSettingManager.getInstance().pageBackground)
        //设置状态栏背景颜色
        StatusBarUtil.setColor(this, ReaderSettingManager.getInstance().getPageBackground())

        pv_read.setTouchListener(object : PageView.TouchListener {
            override fun center() {
                toggleMenu(true)
                if (appbar.isVisible) {
                    //暂停
                    // mSpeakDialog?.tts?.pause()
                    mSpeakDialog?.pause()
                } else {
                    // mSpeakDialog?.tts?.resume()
                    mSpeakDialog?.resume()
                }

            }

            override fun cancel() {

            }
        })
//        pv_read.setOnTouchListener(View.OnTouchListener { v, event ->
//            if (appbar.getVisibility() === View.VISIBLE) {
//                hideReadMenu()
//                return@OnTouchListener true
//            }
//            false
//        })
        //loaddata
        //加载目录

        sectionAdapter = BookSectionAdapter(R.layout.list_item_book_section, mbook.novelList)


        sectionAdapter.setTextColor(pv_read.textColor)
        read_rv_section.setLayoutManager(LinearLayoutManager(this))
        read_rv_section.adapter = sectionAdapter
        tv_title.text = mbook.name

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
        mTopOutAnim.setDuration(300)
        mBottomOutAnim.setDuration(300)

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

            http().mApiService.addShujia(mbook.articleId, mbook.chapterId)
                .compose(RxHttpUtil.handleResult2(mContext as LifecycleOwner))
                .get4(isShowDialog = true, next = {
                    EventBus.getDefault().post(ShujiaEvent(mbook))
                    toast("已加入书架")
                })
        }

        pv_read.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageChange(pos: Int) {
                LogUtils.e("页面改变$pos")
                mSectionItem.currentPage = pos
                //在这里预加载下一章 上一章
                postDelayed(300) {
                    speakByhand()//判断是否语音播报
                }
            }

            override fun onChapterChange(pos: Int) {
                LogUtils.e("章节改变$pos")
                mSectionItem = mbook.novelList.get(pos)
                mbook.currentSetion = pos
                sectionAdapter.setSingleChoosed(pos) //选中当前章节
                read_rv_section.scrollToPosition(pos)
                //章节切换的时候自动加载上一章下一章
                addPreZhangjie()
                addNextZhangjie()

            }

            override fun onPageCountChange(count: Int) {
                LogUtils.e("页面页数改变$count")

            }
        })

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
            pv_read.getmPageLoader().skipPreChapter()
            speakByhand()
        }
        read_tv_next_chapter.click {
            pv_read.getmPageLoader().skipNextChapter()
            speakByhand()

        }
        iv_close_ac.click {
            finish()
        }
        iv_setting.click {

            start(SourceActivity::class.java, Bundle().apply {
                putSerializable("data", mbook)
            })
        }
        read_sb_chapter_progress.max = mbook.novelList.size - 1
        read_sb_chapter_progress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mbook.currentSetion = progress
                mSectionItem = mbook.novelList[progress]
                tv_zhangjie.setText(mSectionItem.title)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                flzhangjie.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                flzhangjie.visibility = View.GONE
                read()
                read_rv_section.scrollToPosition(mbook.novelList.indexOf(mSectionItem))
                sectionAdapter.setSingleChoosed(mbook.novelList.indexOf(mSectionItem)) //选中当前章节

            }

        })
        read_tv_speak.click {

            hideReadMenu()
            hideSystemBar()
            //内容分页
            var content = getPageText()
            if (mSpeakDialog == null) {
                mSpeakDialog = SpeakDialog(mContext, content) {
                    //回调
                    pv_read.autoNextPage() //也会出发页面改变的回调
                    mSpeakDialog?.setText(getPageText())
                    mSpeakDialog?.speak()
                }
            } else {
                // 已经初始化过
                if (getPageText().equals(mSpeakDialog?.texts)) {
                    //没翻页
                    mSpeakDialog?.resume()
                }
                else {
                    //说明已经翻页
                    mSpeakDialog?.setText(getPageText())
                    mSpeakDialog?.speak()
                }
            }

            mSpeakDialog?.show()


        }
        //章节 里面的点击事件
        sectionAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_download) {
                //缓存选中章节
                mbook.novelList.get(position).isLoading = true
                sectionAdapter.notifyItemChanged(position)

                getContentByChap(mbook.novelList.get(position), position, false)
                {
                    //刷新章节列表
                    mbook.novelList.get(position).isLoading = false

                    sectionAdapter.notifyItemChanged(position)
                }
            }
        }
        //选中章节
        sectionAdapter.setOnItemClickListener { adapter, view, position ->
            read_drawer.closeDrawers()
            sectionAdapter.setSingleChoosed(position)
            mbook.currentSetion = position //保存位置
            mSectionItem = mbook.novelList.get(position)
            hideSystemBar()
            //选中过后同时加载前后两章节
            postDelayed(time = 300) {
                requestZhangjie(position, true)
                addPreZhangjie()
                addNextZhangjie()
            }
        }

        pv_read.setPrepareListener {
            //开始阅读
            //   postDelayed {
            if (mbook.currentSetion != 0) {
                //渡过
            } else {
                //没读过
            }
            read()
            //  }
        }
    }


    //手动翻页的时候 播放语音
    fun speakByhand() {
        mSpeakDialog?.let {
            if (it.isReading) {
                it.setText(getPageText())
                it.speak()
            }
        }
    }

    fun getPageText(): String {
        var content = ""
        pv_read.getmPageLoader().getmCurPage().lines.forEach {
            content = content + it
        }
        return content
    }

    private fun read() {

        getContentByChap(mSectionItem, mbook.currentSetion, true) {
            pv_read.openSection(mbook.currentSetion, mSectionItem.currentPage) //从网络获取的就从第一页读
        }
        //预加载前一张和下一章
        addPreZhangjie()
        addNextZhangjie()
    }

    fun addPreZhangjie() {
        //加载上一章
        var p = mbook.currentSetion - 1
        if (p >= 0)

            getContentByChap(mbook.novelList.get(p), p, false) {
            }
    }

    fun addNextZhangjie() {
        //加载下一章
        var p = mbook.currentSetion + 1
        if (p < mbook.novelList.size)

            getContentByChap(mbook.novelList.get(p), p, false) {

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

    fun requestZhangjie(p: Int, isShoaDialog: Boolean = false) {
//加载当前章节
        var xuanzhong = mbook.novelList.get(p)

        getContentByChap(xuanzhong, p, isShoaDialog) {
            pv_read.openSection(p, mSectionItem.currentPage) //从网络获取的就从第一页读

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        //保存当前进度
        App.instance.db.getBookDao().update(mbook)
        App.instance.db.getChapDao().updata(mSectionItem) //记录当前章节进度
        mSpeakDialog?.stopTTs()
    }

    // next 为获取网络结果后的回调
    fun getContentByChap(
        bookSectionItem: BookSectionItem,
        p: Int,
        isShowtitle: Boolean = false,
        next: () -> Unit
    ) {
        //已经从网络获取过内容
        if (!bookSectionItem.content.isNullOrEmpty()) {
            //  没有加载过才加载
            if (!readAdapter.hasSection(p)) {
                readAdapter.addData(
                    p,
                    BookSectionContent(p, bookSectionItem.title, bookSectionItem.content)
                )
            }
            //自定义的操作
            next()
        } else {
            //还没加载过 从网路获取
            http().mApiService.openChap(mbook.articleId, bookSectionItem.chapterId)
                .get3(isShowDialog = isShowtitle, title = "", message = "加载章节中……") {
                    var content = it?.data?.content?.replace("<br>", "")
                    content = content?.replace("&nbsp;", "")
                    bookSectionItem.content = content//改变了mbook书里面的内容
                    App.instance.db.getChapDao().updata(bookSectionItem) //保存到数据库

                    //添加章节
                    readAdapter.addData(
                        p,
                        BookSectionContent(p, bookSectionItem.title, bookSectionItem.content)
                    )
                    //自定义的操作
                    next()
                }
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
//        SystemBarUtils.hideStableStatusBar(this)
//        if (isFullScreen) {
//            SystemBarUtils.hideStableNavBar(this)
//        }
    }

    //初始化菜单动画
    private fun initMenuAnim() {

        mBottomInAnim?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                //  pv_read.setCanTouch(false)
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
                //   pv_read.setCanTouch(true)

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private fun toggleMenu(hideStatusBar: Boolean): Boolean {
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
        return appbar.isVisible
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
//        SystemBarUtils.showUnStableStatusBar(this)
//        if (isFullScreen) {
//            SystemBarUtils.showUnStableNavBar(this)
//        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_read
    }


}
