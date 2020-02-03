package com.dove.readandroid.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.annotation.CallSuper
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import com.appbaselib.base.BaseMvcActivity
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import com.dove.readandroid.R
import com.just.agentweb.WebViewClient
import com.safframework.ext.click
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : BaseMvcActivity() {

    override fun getLoadingTargetView(): View? {
        return null
    }

    lateinit var url: String
    lateinit var mAgentWeb: AgentWeb
    var isShow = true

    companion object {
        fun instance(url: String?, context: Context, isShowTitleBar: Boolean = true) {
            context.startActivity(Intent(context, WebViewActivity::class.java).apply {
                putExtra("url", url)
                putExtra("isshow", isShowTitleBar)
            })
        }
    }

    @CallSuper
    override fun initView(mSavedInstanceState: Bundle?) {
        url = intent?.getStringExtra("url") ?: ""
        if (intent?.getBooleanExtra("isshow", true)!!) {
            title_layout.visibility = View.VISIBLE
        } else {
            title_layout.visibility = View.GONE
        }
        initOther()
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(findViewById(R.id.content) as FrameLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            .useDefaultIndicator()
            .setWebViewClient(Client(this))
            .setWebChromeClient(DefaultWebChromeClient())
            .createAgentWeb()
            .ready()
            .go(url)

        val webSetting = this.mAgentWeb.webCreator.webView.settings
        webSetting.setJavaScriptEnabled(true)
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true)
        webSetting.setAllowUniversalAccessFromFileURLs(true)
        webSetting.setAllowFileAccess(true)
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS)
        webSetting.setSupportZoom(false)
        webSetting.setBuiltInZoomControls(true)
        webSetting.setUseWideViewPort(true)
        webSetting.setSupportMultipleWindows(true)
        webSetting.setSavePassword(false)
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND)
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE)
        webSetting.setLoadWithOverviewMode(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
        }
        webSetting.setDomStorageEnabled(true)
//        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//        String appCachePath = this.getContext().getApplicationContext().getCacheDir().getAbsolutePath();
//        webSetting.setAppCachePath(appCachePath);
//        webSetting.setAppCacheEnabled(true);

        webSetting.setAllowFileAccess(true)
        WebView.setWebContentsDebuggingEnabled(true)

        mAgentWeb.jsInterfaceHolder.addJavaObject("dove", JavaScriptInterface(this))

        iv_left.click {
            if (!mAgentWeb.back()) {
                finish()
            }
        }
        tv_close.click {
            finish()
        }
    }

    open fun initOther() {

    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_web_view

    }

    override fun onPause() {
        mAgentWeb.webLifeCycle.onPause()
        super.onPause()

    }

    override fun onResume() {
        mAgentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        return if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }


    inner class Client(private var activity: Activity) : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val mUri = Uri.parse(url)
            JsBridge(view, mUri, activity).process()

            //Android 8.0以下版本的需要返回true 并且需要loadUrl()
            if (Build.VERSION.SDK_INT < 26) {
                return true;
            }
            return false;
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val mUri = request.url
            JsBridge(view, mUri, activity).process()

            //Android 8.0以下版本的需要返回true 并且需要loadUrl()
            if (Build.VERSION.SDK_INT < 26) {
                return true;
            }
            return false;
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            if (!mAgentWeb.webCreator.webView.canGoBack()) {
                tv_close.visibility = View.GONE
            } else {
                tv_close.visibility = View.VISIBLE
            }

            super.onPageFinished(view, url)
        }
    }
}
