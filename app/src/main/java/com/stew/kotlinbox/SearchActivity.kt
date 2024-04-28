package com.stew.kotlinbox

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val iv_back: ImageView = findViewById(R.id.iv_back)
        iv_back.setOnClickListener {
            onBackPressed()
        }

        val tv_search: TextView = findViewById(R.id.tv_search)
        tv_search.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(tv_search.windowToken, 0)
        }
    }
}