package com.stew.kotlinbox

import ArticleDetailBean
import DBHelper
import SearchHistoryBean
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.stew.kb_common.util.AppCommonUitl
import com.stew.kb_common.util.AppCommonUitl.pxToDp
import com.stew.kb_common.util.Constants
import com.stew.kb_common.util.KVUtil
import com.stew.kb_home.adapter.HomeItemClickListener
import com.stew.kb_home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private var searchResultRV: RecyclerView? = null
    private var searchHistoryLL: LinearLayout? = null
    private var editText: EditText? = null
    private var searchHistorySV: ScrollView? = null
    private var searchTV: TextView? = null
    private var backIV: ImageView? = null

    private val homeViewModel: HomeViewModel by viewModel()
    private var collectPosition: Int = 0
    private var list = listOf<ArticleDetailBean>()
    private val searchRecords = mutableSetOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
        initEvent()
    }

    private fun initView() {
        searchResultRV = findViewById(R.id.search_article_result_rv)
        editText = findViewById(R.id.editText_search)
        searchHistorySV = findViewById(R.id.search_history_SV)
        searchTV = findViewById(R.id.tv_search)
        backIV = findViewById(R.id.iv_back)
        searchHistoryLL = findViewById(R.id.container)
    }

    private fun initEvent() {
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
        backIV?.setOnClickListener {
            onBackPressed()
        }
        searchTV?.setOnClickListener {
            val view = this.currentFocus
            if (view != null) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchTV?.windowToken, 0)
            }
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
                DBHelper.getInstance(AppCommonUitl.appContext)
                    .insertSearchHistory(SearchHistoryBean(username, searchString))
                searchRecords.add(searchString)
            }
            val dbHelper = DBHelper.getInstance(AppCommonUitl.appContext)
            list = dbHelper.searchArticlesByTitle(editText?.text.toString())
            searchRVAdapter.setData(list)
            searchResultRV?.adapter = searchRVAdapter
            searchResultRV?.layoutManager = LinearLayoutManager(this)
            editText?.clearFocus()
        }
        editText?.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                searchHistorySV?.visibility = View.VISIBLE
                val dbHelper = DBHelper.getInstance(this)
                dbHelper.getSearchHistoryForUser(KVUtil.getString(Constants.USER_NAME) ?: "tourist")
                    .forEach { searchHistoryBean ->
                        searchRecords.add(searchHistoryBean.searchString)
                    }
                searchRecords.forEach {
                    addSearchRecord(it)
                }
            } else{
                searchHistoryLL?.removeAllViews()
                searchHistorySV?.visibility = View.GONE
            }
        }
    }

    private fun addSearchRecord(recordText: String) {
        val frameLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val textView = TextView(this).apply {
            text = recordText
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setTextSize(16f)
                setMargins(30, 0, 0, 0)
                gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            }
        }
        frameLayout.addView(textView)

        val deleteButton = Button(this).apply {
            text = "X"
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.END
            }
            setOnClickListener {
                searchHistoryLL?.removeView(frameLayout)
                searchRecords.remove(recordText)
                DBHelper.getInstance(this@SearchActivity)
                    .deleteSearchHistoryBySearchString(recordText)
            }
        }
        frameLayout.setOnClickListener {
            editText?.setText(recordText)
            editText?.setSelection(recordText.length)
        }
        frameLayout.addView(deleteButton)
        searchHistoryLL?.addView(frameLayout)
    }
}