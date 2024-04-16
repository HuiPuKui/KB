package com.stew.kb_user.repo

import com.stew.kb_common.base.BaseRepository
import com.stew.kb_common.network.RespStateData
import com.stew.kb_user.api.UserApi
import com.stew.kb_user.bean.RegisterBean

/**
 * Created by stew on 8/21/22.
 * mail: stewforani@gmail.com
 */
class RegisterRepo(private val api: UserApi) : BaseRepository() {
    suspend fun register(username: String, password: String, repassword: String, data: RespStateData<RegisterBean>) =
        dealResp(
            block = { api.register(username, password, repassword) }, data
        )

}