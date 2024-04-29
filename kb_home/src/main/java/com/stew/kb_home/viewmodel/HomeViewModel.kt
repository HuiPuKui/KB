package com.stew.kb_home.viewmodel

import ArticleDetailBean
import DBHelper
import android.util.Log
import com.stew.kb_common.base.BaseViewModel
import com.stew.kb_common.network.BaseResp
import com.stew.kb_common.network.RespStateData
import com.stew.kb_common.util.AppCommonUitl
import com.stew.kb_home.bean.Article
import com.stew.kb_home.bean.Banner
import com.stew.kb_home.repo.HomeRepo
import kotlinx.coroutines.flow.zip

/**
 * Created by stew on 7/30/22.
 * mail: stewforani@gmail.com
 */
class HomeViewModel(private val repo: HomeRepo) : BaseViewModel() {

    var bannerList = RespStateData<List<Banner>>()
    var article = RespStateData<Article>()
    var collectData = RespStateData<String>()

    fun getBanner() = launch {
        Log.d("TestSus", "vm1")
        repo.getBanner(bannerList)
        Log.d("TestSus", "vm2")
    }

    fun getArticle(currentPage: Int) = launch { repo.getArticle(currentPage, article) }
    fun collect(id: Int) = launch { repo.collect(id, collectData) }
    fun unCollect(id: Int) = launch { repo.unCollect(id, collectData) }


    //flow test
    fun getDataByFlow() = launch {

        val startTime = System.currentTimeMillis()

//        repo.getBannerByFlow().onStart {
//            Log.d("HomeViewModel", "getBannerByFlow start")
//        }.onCompletion {
//            Log.d("HomeViewModel", "getBannerByFlow end")
//        }.collectLatest {
//            it.responseState = BaseResp.ResponseState.REQUEST_SUCCESS
//            bannerList.value = it
//            Log.d("HomeViewModel", "getBannerByFlow success")
//        }
        val dbHelper = DBHelper.getInstance(AppCommonUitl.appContext)

        repo.getBannerByFlow().zip(repo.getArticleByFlow(0)) { Bbanner, Barticle ->
            Bbanner.responseState = BaseResp.ResponseState.REQUEST_SUCCESS
            bannerList.value = Bbanner
            Barticle.responseState = BaseResp.ResponseState.REQUEST_SUCCESS
            article.value = Barticle

            if (Barticle.data != null) {
                val articleData = Barticle.data as Article
                val articleDetailListData = articleData.datas
                articleDetailListData.forEach {
                    dbHelper.insertArticle(
                        ArticleDetailBean(
                            author = it.author,
                            fresh = it.fresh,
                            articleId = it.id,
                            link = it.link,
                            niceDate = it.niceDate,
                            shareUser = it.shareUser,
                            title = it.title,
                            superChapterId = it.superChapterId,
                            superChapterName = it.superChapterName,
                            collect = it.collect
                        )
                    )
                }

            }
        }.collect {
            Log.d("FlowTest", "Time: " + (System.currentTimeMillis() - startTime))
        }
        Log.w("getAllData", "getDataByFlow: ${dbHelper.getAllArticles()}")

    }
}