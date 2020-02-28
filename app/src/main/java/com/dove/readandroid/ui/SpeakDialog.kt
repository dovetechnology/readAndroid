package com.dove.readandroid.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import com.appbaselib.ext.toast
import com.appbaselib.utils.PreferenceUtils

import com.dove.readandroid.R
import com.dove.readandroid.ui.common.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iflytek.cloud.*
import com.iflytek.cloud.msc.util.FileUtil
import com.iflytek.cloud.msc.util.log.DebugLog
import com.safframework.ext.click
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_fenlei_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_read_speak.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/2/27 16:21
 * ===============================
 */
class SpeakDialog(context: Context, var texts: String, var complete: (() -> Unit)) :
    BottomSheetDialog(context, R.style.Read_Setting_Dialog) {

    // 语音合成对象
    lateinit var mTts: SpeechSynthesizer
    // 缓冲进度
    private var mPercentForBuffering = 0
    // 播放进度
    private var mPercentForPlaying = 0
    private val container = Vector<ByteArray>()
    private var mEngineType = SpeechConstant.TYPE_CLOUD

    public var isReading = false;

    init {
        super.setContentView(R.layout.bottom_sheet_read_speak)
        initView()
    }

    fun setText(text: String) {
        texts = text
    }

    fun speak() {
        setParam()
        var code = mTts?.startSpeaking(texts, mTtsListener)
        if (code != ErrorCode.SUCCESS) {
            toast("语音合成失败,错误码: $code")
        }
    }

    private fun initView() {


        read_yinliang.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        tv_tuichu.click {
            //    stopTTs()
            //暂停
            isReading = false
            mTts.pauseSpeaking()
            dismiss()
        }

        voicer = PreferenceUtils.getPrefString(context, SpeechConstant.VOICE_NAME, "xiaoyan")
        yusu = PreferenceUtils.getPrefString(context, Constants.SPEECH_SPEED, "50")
        if (voicer.equals("xiaoyan")) {
            read_nvsheng.isSelected = true;//默认
        } else {
            read_tv_nansheng.isSelected = true;//默认是开启的
        }
        read_nvsheng.click {
            voicer = "xiaoyan"
            //重新读本章节
            read_nvsheng.isSelected = true
            read_tv_nansheng.isSelected = false
            PreferenceUtils.setPrefString(context, SpeechConstant.VOICE_NAME, voicer)
            speak()

        }
        read_tv_nansheng.click {
            voicer = "xiaoyu"
            //重新读本章节
            read_nvsheng.isSelected = false
            read_tv_nansheng.isSelected = true
            PreferenceUtils.setPrefString(context, SpeechConstant.VOICE_NAME, voicer)
            speak()

        }
        read_yinliang.setProgress(yusu.toInt())
        read_yinliang.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                yusu = seekBar?.progress.toString()
                //重新读本章节
                PreferenceUtils.setPrefString(context, Constants.SPEECH_SPEED, yusu)
                speak()
            }

        })
        read_tv_shiwu.click {
            dingshi(15)
            read_tv_shiwu.isSelected = true
            read_tv_sanshi.isSelected = false
            read_tv_liushi.isSelected = false
            read_tv_jiushi.isSelected = false

        }
        read_tv_sanshi.click {
            dingshi(30)
            read_tv_shiwu.isSelected = false
            read_tv_sanshi.isSelected = true
            read_tv_liushi.isSelected = false
            read_tv_jiushi.isSelected = false
        }
        read_tv_liushi.click {
            dingshi(60)
            read_tv_shiwu.isSelected = false
            read_tv_sanshi.isSelected = false
            read_tv_liushi.isSelected = true
            read_tv_jiushi.isSelected = false
        }
        read_tv_jiushi.click {
            dingshi(90)
            read_tv_shiwu.isSelected = false
            read_tv_sanshi.isSelected = false
            read_tv_liushi.isSelected = false
            read_tv_jiushi.isSelected = true
        }

        //初始化
        mTtsInitListener = InitListener { code ->
            Log.d("tts------>", "InitListener init() code = $code")
            if (code != ErrorCode.SUCCESS) {
                toast("初始化失败,错误码：$code")
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
                tv_tuichu.isEnabled = true
            }
            speak()
        }
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener)
    }

    private var mdDisposable: Disposable? = null

    private fun dingshi(time: Long) {
        mdDisposable?.dispose()
        mdDisposable = null
        mdDisposable = Flowable.timer(time, TimeUnit.MINUTES)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mTts.pauseSpeaking()
                // stopTTs()
            }
    }

    fun stopTTs() {
        if (null != mTts) {
            mTts.stopSpeaking()
            // 退出时释放连接
            mTts.destroy()
        }
    }

    /**
     * 初始化监听。
     */
    lateinit var mTtsInitListener: InitListener

    /**
     * 合成回调监听。
     */
    private val mTtsListener = object : SynthesizerListener {

        override fun onSpeakBegin() {
            toast("开始播放")
            isReading = true
        }

        override fun onSpeakPaused() {
            toast("暂停播放")
            isReading = false
        }

        override fun onSpeakResumed() {
            toast("继续播放")
            isReading = true
        }

        override fun onBufferProgress(
            percent: Int, beginPos: Int, endPos: Int,
            info: String
        ) {
            // 合成进度
            Log.e("MscSpeechLog_", "percent =$percent")
            mPercentForBuffering = percent
        }

        override fun onSpeakProgress(percent: Int, beginPos: Int, endPos: Int) {
            // 播放进度
            Log.e("MscSpeechLog_", "percent =$percent")
            mPercentForPlaying = percent

        }

        override fun onCompleted(error: SpeechError?) {
            println("oncompleted")
            if (error == null) {
                //	toast("播放完成");

            } else if (error != null) {
                //    toast(error.getPlainDescription(true))
            }
            complete() //播放完成回调下一章
        }

        override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
            //	 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                val sid = obj?.getString(SpeechEvent.KEY_EVENT_SESSION_ID)
                Log.d("tag-->", "session id =" + sid!!)
            }
            //当设置SpeechConstant.TTS_DATA_NOTIFY为1时，抛出buf数据
            if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
                val buf = obj?.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER)
                Log.e("MscSpeechLog_", "bufis =" + buf!!.size)
                container.add(buf)
            }


        }
    }
    private var voicer = "xiaoyan"
    private var yusu = "50"

    private fun setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null)
        // 根据合成引擎设置相应参数
        if (mEngineType == SpeechConstant.TYPE_CLOUD) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
            //支持实时音频返回，仅在synthesizeToUri条件下支持
            mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1")
            //	mTts.setParameter(SpeechConstant.TTS_BUFFER_TIME,"1");

            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer)
            //设置合成语速
            mTts.setParameter(
                SpeechConstant.SPEED,
                PreferenceUtils.getPrefString(context, Constants.SPEECH_SPEED, yusu)
            )
            //设置合成音调
            mTts.setParameter(
                SpeechConstant.PITCH,
                PreferenceUtils.getPrefString(context, Constants.PITCH_SPEED, "50")

            )
            //设置合成音量
            mTts.setParameter(
                SpeechConstant.VOLUME,
                PreferenceUtils.getPrefString(context, Constants.VOLUME_SPEED, "50")

            )
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL)
            mTts.setParameter(SpeechConstant.VOICE_NAME, "")

        }

        //设置播放器音频流类型
        mTts.setParameter(
            SpeechConstant.STREAM_TYPE,
            PreferenceUtils.getPrefString(context, Constants.STREAM_SPEED, "3")
        )
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false")

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm")
        mTts.setParameter(
            SpeechConstant.TTS_AUDIO_PATH,
            Environment.getExternalStorageDirectory().toString() + "/msc/tts.pcm"
        )
    }

}
