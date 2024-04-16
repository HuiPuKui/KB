package com.stew.kotlinbox

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.stew.kb_common.base.BaseActivity
import com.stew.kb_common.util.AppCommonUitl
import com.stew.kb_common.util.Constants
import com.stew.kb_common.util.Extension.dp2px
import com.stew.kb_common.util.KVUtil
import com.stew.kb_home.ui.HomeFragment
import com.stew.kb_me.ui.MyCollectFragment
import com.stew.kb_navigation.ui.MainFragment
import com.stew.kb_project.ui.ProjectFragment
import com.stew.kotlinbox.databinding.ActivityMainBinding


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var fragmentList: MutableList<Fragment>
    var oldFragmentIndex: Int = 0
    private var pw: PopupWindow? = null


    var isNight = false
    var f1: Fragment? = null
    var f2: Fragment? = null
    var f3: Fragment? = null
    var f4: Fragment? = null

    override fun getLayoutID(): Int {
        return R.layout.activity_main
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged: ")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: $intent")
    }

    override fun init() {
        var framelayout: FrameLayout = findViewById(R.id.framwlayout)
        var alreadyLogin: LinearLayout = findViewById(R.id.already_login)
        var noneLogin: LinearLayout = findViewById(R.id.none_login)

        mBind.imgDraw.setOnClickListener {
            if (KVUtil.getString(Constants.USER_NAME) != null) {
                framelayout.removeAllViews()
                framelayout.addView(alreadyLogin)
            } else {
                framelayout.removeAllViews()
                framelayout.addView(noneLogin)
            }
            mBind.dl.open()
        }


        mBind.imgBox.setOnClickListener {
            showPop()
        }

        mBind.bnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.f1 -> {
                    switchFragment(0)
                    mBind.fName = resources.getString(R.string.tab_home)
                    return@setOnItemSelectedListener true
                }

                R.id.f2 -> {
                    switchFragment(1)
                    mBind.fName = resources.getString(R.string.tab_project)
                    return@setOnItemSelectedListener true
                }

                R.id.f3 -> {
                    switchFragment(2)
                    mBind.fName = resources.getString(R.string.tab_navi)
                    return@setOnItemSelectedListener true
                }

                R.id.f4 -> {
                    switchFragment(3)
                    mBind.fName = resources.getString(R.string.tab_collect)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }



        initFragment()
        fragmentList = mutableListOf(f1!!, f2!!, f3!!, f4!!)

        switchFragment(0)
        mBind.fName = resources.getString(R.string.tab_home)

        val tv_dark1: TextView = findViewById(R.id.tv_dark1)
        tv_dark1.setOnClickListener {
            switchDarkMode()
        }

        val tv_dark2: TextView = findViewById(R.id.tv_dark2)
        tv_dark2.setOnClickListener {
            switchDarkMode()
        }
    }

    private fun initFragment() {
        if (f1 == null) {
            f1 = HomeFragment()
        }
        if (f2 == null) {
            f2 = ProjectFragment()
        }
        if (f3 == null) {
            f3 = MainFragment()
        }
        if (f4 == null) {
            f4 = MyCollectFragment()
        }
    }


    private fun showPop() {
        if (pw == null) {
            val v = LayoutInflater.from(this).inflate(R.layout.layout_main_pop, null, false)
            pw = PopupWindow(
                v, 160.dp2px(),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )
            pw!!.isOutsideTouchable = true
            v.findViewById<LinearLayout>(R.id.ll_exp).setOnClickListener {
                pw!!.dismiss()
                ARouter.getInstance()
                    .build(Constants.PATH_EXP)
                    .navigation()
            }
        }
        val x = resources.displayMetrics.widthPixels - pw!!.width - 20
        pw!!.showAtLocation(mBind.imgBox, Gravity.NO_GRAVITY, x, 80.dp2px())
    }

    private fun switchDarkMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCommonUitl.setAppDarkMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCommonUitl.setAppDarkMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun switchFragment(position: Int) {

        val targetFragment = fragmentList[position]
        val oldFragment = fragmentList[oldFragmentIndex]
        oldFragmentIndex = position
        val ft = supportFragmentManager.beginTransaction()

        if (oldFragment.isAdded) {
            ft.hide(oldFragment)
        }

        if (!targetFragment.isAdded) {
            ft.add(R.id.f_content, targetFragment)
        }

        ft.show(targetFragment).commitAllowingStateLoss()
    }


    override fun onResume() {
        super.onResume()
        val name = KVUtil.getString(Constants.USER_NAME)
        if (name != null) {
            findViewById<TextView>(R.id.tx_info).text = "NickName: $name"
        }
    }

}
