package com.dove.readandroid.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log

import com.appbaselib.utils.ToastUtils

import java.util.HashMap

class ChinaTTSManager(context: Context, private var text: String = "", var next: () -> Unit) :
    WhyTTS {
    var mSpeech: TextToSpeech? = null
    private var residenceContent = ""
    private val sentenceStep = HashMap<Int, Long>()
    private var startTime: Long = 0
    private var duration: Long = 0
    private var charStep: Long = 225
    private val markStep: Long = 300

    fun setText(text: String): ChinaTTSManager {
        this.text = text
        return this
    }

    init {
        mSpeech = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            //mSpeech.setSpeechRate(1.0f); 默认就是1.0f
            if (status == TextToSpeech.SUCCESS) {
                speak(text)
                next()
            } else {
                ToastUtils.showShort(context, "语音初始化失败")
            }
        })
    }

    override fun speak(content: String) {
        residenceContent = content
        //sentenceStep.clear();
        //getSentenceStep();
        startTime = System.currentTimeMillis()
        mSpeech!!.speak(content, TextToSpeech.QUEUE_FLUSH, null, "id")
    }

    override fun pause() {
        duration = System.currentTimeMillis() - startTime
        residenceContent = getResidenceByDuration(duration)
        mSpeech!!.speak("", TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun resume() {
        speak(residenceContent)
    }

    override fun stop() {
        mSpeech!!.stop()
        mSpeech!!.shutdown()
    }

    override fun setSpeechRate(newRate: Float) {
        if (mSpeech != null) {
            mSpeech!!.setSpeechRate(newRate)
            //TODO update charStep
            charStep = charStep + (newRate - 1.0f).toLong() * charStep
        }
    }

    override fun setSpeechPitch(newPitch: Float) {
        if (mSpeech != null) {
            mSpeech!!.setSpeechRate(newPitch)
        }
    }

    private fun getResidenceByDuration(duration: Long): String {
        val tempIndex = (duration / charStep).toInt()
        if (duration > charStep * residenceContent!!.length) {
            return ""
        }
        if (tempIndex == 0) {
            return residenceContent
        }
        else {
            residenceContent = residenceContent!!.substring(tempIndex - 1)
            return residenceContent
        }
        //        int index=findSentenceIndex(duration);
        //        if(index==-1){
        //            return "";
        //        }else {
        //            residenceContent=residenceContent.substring((int)((duration-sentenceStep.get(index-1))/charStep)+1);
        //            return residenceContent;
        //        }
    }

    /**
     * 根据朗读时间计算当前读到句子索引
     *
     * @param duration
     */
    private fun findSentenceIndex(duration: Long): Int {
        for (i in 0 until sentenceStep.size - 1) {
            if (duration <= sentenceStep[i + 1]!! && duration >= sentenceStep[i]!!) {
                return i + 1
            }
        }
        return if (duration > sentenceStep[sentenceStep.size - 1]!!) {
            -1
        } else -2
    }

    /**
     * get the mark index list
     */
    private fun getSentenceStep() {
        val array =
            residenceContent!!.split("，".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (array.size <= 1) {
            return
        } else {
            for (i in array.indices) {
                var tempTime: Long = 0
                for (j in 0..i) {
                    tempTime += array[j].length * charStep
                }
                tempTime += (i + 1) * markStep
                sentenceStep[i] = tempTime
            }
        }
    }

    companion object {

        private val TAG = "ChinaTTSManagerWhy"
        private val whyTTS: WhyTTS? = null
    }
}
