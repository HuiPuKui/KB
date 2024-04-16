package com.stew.kb_user.viewmodel

import com.stew.kb_common.base.BaseViewModel
import com.stew.kb_common.network.RespStateData
import com.stew.kb_user.bean.RegisterBean
import com.stew.kb_user.repo.RegisterRepo
/**
 * Created by stew on 8/21/22.
 * mail: stewforani@gmail.com
 */
class RegisterViewModel(private val repo: RegisterRepo) : BaseViewModel() {

    var registerData = RespStateData<RegisterBean>()
    fun register(username: String, password: String, repassword: String) {
        launch(
            block = {
                repo.register(username, password, repassword, registerData)
            }
        )
    }
}