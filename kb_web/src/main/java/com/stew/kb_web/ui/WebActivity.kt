package com.stew.kb_web.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.stew.kb_common.base.BaseActivity
import com.stew.kb_common.util.Constants
import com.stew.kb_web.R
import com.stew.kb_web.databinding.ActivityWebBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.NullPointerException

/**
 * Created by stew on 8/18/22.
 * mail: stewforani@gmail.com
 */

@Route(path = Constants.PATH_WEB)
class WebActivity : BaseActivity<ActivityWebBinding>() {

    lateinit var web: WebView

    override fun getLayoutID(): Int {
        return R.layout.activity_web
    }


    override fun init() {
        if (intent == null) {
            return
        }
        val t = intent.getStringExtra(Constants.WEB_TITLE)
        val l = intent.getStringExtra(Constants.WEB_LINK).toString()

        mBind.imgBack.setOnClickListener { finish() }
        mBind.txWebTitle.text = t

        //模拟崩溃
        //--------------------------------F-----------------
//        lifecycleScope.launch {
//            delay(5000)
//            val a = mutableListOf<Int>()
//            a[100]
//        }
        //-------------------------------------------------

        web = WebView(applicationContext)
        mBind.webContainer.addView(web)

        web.settings.javaScriptEnabled = true
        web.settings.loadWithOverviewMode = true
        web.settings.domStorageEnabled = true

        web.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "onPageStarted: ")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "onPageFinished: ")
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: =====2")
                if (!l.equals(request?.url)) {
                    Log.d(TAG, "shouldOverrideUrlLoading: =====3")
                    startActivity(
                        //如果没有自带浏览器，会崩溃，这里可以做一下规避处理
                        Intent(Intent.ACTION_VIEW, Uri.parse(request?.url.toString()))
                    )
                    return true
                }
                return false
            }
        }

        web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.d(TAG, "onProgressChanged: $newProgress")
                mBind.pb.progress = newProgress
                if (newProgress == 100) {
                    mBind.pb.visibility = View.GONE
                }
            }
        }

        web.loadUrl(l)

    }

    override fun onDestroy() {
        mBind.webContainer.removeAllViews()
        web.clearHistory()
        web.destroy()
        super.onDestroy()
    }
}