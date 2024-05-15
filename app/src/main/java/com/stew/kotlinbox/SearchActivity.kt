package com.stew.kotlinbox

import ArticleDetailBean
import SearchHistoryBean
import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.stew.kb_common.util.AppCommonUitl
import com.stew.kb_common.util.Constants
import com.stew.kb_common.util.KVUtil
import com.stew.kb_home.adapter.HomeItemClickListener
import com.stew.kb_home.viewmodel.HomeViewModel
import okhttp3.internal.notifyAll
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private var searchResultRV: RecyclerView? = null
    private var editText:EditText?=null
    private val homeViewModel: HomeViewModel by viewModel()
    var collectPosition: Int = 0
    var list = listOf<ArticleDetailBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchResultRV = findViewById(R.id.search_article_result_rv)
        editText = findViewById(R.id.editText_search)
        val searchRVAdapter = SearchRVAdapter(object : HomeItemClickListener {
            override fun onItemClick(position: Int) {
                val data = list[position]
                ARouter.getInstance()
                    .build(Constants.PATH_WEB)
                    .withString(Constants.WEB_LINK, data.link)
                    .withString(Constants.WEB_TITLE, data.title)
                    .navigation()
            }

            override fun onCollectClick(position: Int) {
                collectPosition = position
                if (list[collectPosition].collect) {
                    homeViewModel.unCollect(list[collectPosition].articleId)
                } else {
                    homeViewModel.collect(list[collectPosition].articleId)
                }
            }

        })
        val iv_back: ImageView = findViewById(R.id.iv_back)
        iv_back.setOnClickListener {
            onBackPressed()
        }

        val tv_search: TextView = findViewById(R.id.tv_search)
        tv_search.setOnClickListener {

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(tv_search.windowToken, 0)
            val editText_search: EditText = findViewById(R.id.editText_search)
            Log.d("huipukui", editText_search.text.toString())

            var searchString = editText_search.text.toString()
            var username = ""

            if (searchString.length > 0) {
                if (KVUtil.getString(Constants.USER_NAME).isNullOrEmpty()) {
                    username = "tourist"
                } else {
                    username = KVUtil.getString(Constants.USER_NAME).toString()
                }
                Log.d("huipukui", "username: ${username}")
                DBHelper.getInstance(AppCommonUitl.appContext).insertSearchHistory(SearchHistoryBean(username, searchString))
            }
            val dbHelper = DBHelper.getInstance(AppCommonUitl.appContext)
            list = dbHelper.searchArticlesByTitle(editText?.text.toString())
            searchRVAdapter.setData(list)
            searchResultRV?.adapter = searchRVAdapter
            searchResultRV?.layoutManager = LinearLayoutManager(this)
        }
    }
}