package com.stew.kb_exp.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.stew.kb_common.base.BaseActivity
import com.stew.kb_common.util.Constants
import com.stew.kb_common.util.ToastUtil
import com.stew.kb_exp.R
import com.stew.kb_exp.databinding.ActivityExpBinding
import java.io.File
import java.lang.ref.WeakReference

/**
 * Created by stew on 2023/10/18.
 * mail: stewforani@gmail.com
 */
@Route(path = Constants.PATH_EXP)
class ExpActivity : BaseActivity<ActivityExpBinding>() {

    override fun getLayoutID(): Int {
        return R.layout.activity_exp
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged: ")
    }

    override fun init() {
        mBind.imgBack.setOnClickListener { finish() }

        testSth()

    }

    private fun testSth() {

//        Log.d("mem_info_test", Runtime.getRuntime().totalMemory().toString())
//        Log.d("mem_info_test", Runtime.getRuntime().freeMemory().toString())
//        Log.d("mem_info_test", Debug.getNativeHeapSize().toString())
//        Log.d("mem_info_test", Debug.getNativeHeapFreeSize().toString())

//        runBlocking {
//            flow {
//                (1..5).forEach {
//                    //delay(200)
//                    println("emit$it,${System.currentTimeMillis()},${Thread.currentThread().name}")
//                    emit(it)
//                }
//            }.flowOn(Dispatchers.IO).collect {
//                delay(500)
//                println("collect$it,${System.currentTimeMillis()},${Thread.currentThread().name}")
//            }
//        }

//        val flow = flow<Int> {
//            (1..5).forEach {
//                emit(it)
//            }
//        }
//        lifecycleScope.launch {
//            flow.collect {
//                Log.d(TAG, "testFlow 第一个收集器: 我是冷流：$it")
//            }
//        }
//        lifecycleScope.launch {
//            flow.collect {
//                Log.d(TAG, "testFlow:第二个收集器 我是冷流：$it")
//            }
//        }


    }

}