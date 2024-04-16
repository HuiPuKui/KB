package com.stew.kb_user.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stew.kb_common.network.BaseStateObserver
import com.stew.kb_common.util.Constants
import com.stew.kb_common.util.KVUtil
import com.stew.kb_common.util.ToastUtil
import com.stew.kb_user.R
import com.stew.kb_user.bean.LoginBean
import com.stew.kb_user.bean.RegisterBean
import com.stew.kb_user.viewmodel.LoginViewModel
import com.stew.kb_user.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.w3c.dom.Text

class RegisterActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        registerViewModel.registerData.observe(this, object : BaseStateObserver<RegisterBean>(null) {
            override fun getRespDataSuccess(it: RegisterBean) {
                ToastUtil.showMsg("注册成功！")
                finish()
            }
        })

        val backButton: ImageView = findViewById(R.id.img_back)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val registerButton: TextView = findViewById(R.id.tx_register)
        registerButton.setOnClickListener {
            val edit1: EditText = findViewById(R.id.edit1)
            val edit2: EditText = findViewById(R.id.edit2)
            val edit3: EditText = findViewById(R.id.edit3)

            if (edit1.text.isNotEmpty() && edit2.text.isNotEmpty() && edit3.text.isNotEmpty()) {
                registerViewModel.register(edit1.text.toString(), edit2.text.toString(), edit3.text.toString())
            } else {
                ToastUtil.showMsg("输入有误！")
            }
        }
    }
}