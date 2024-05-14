package com.stew.kotlinbox

import ArticleDetailBean
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.stew.kb_common.network.BaseStateObserver
import com.stew.kb_common.util.Constants
import com.stew.kb_common.util.ToastUtil
import com.stew.kb_home.R
import com.stew.kb_home.adapter.BannerAdapter
import com.stew.kb_home.adapter.HomeItemClickListener
import com.stew.kb_home.adapter.HomeRVAdapter
import com.stew.kb_home.bean.Article
import com.stew.kb_home.bean.Banner
import com.stew.kb_home.bean.a
import com.stew.kb_home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchRVAdapter(var listener: HomeItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    private var diff: MutableList<ArticleDetailBean>
    private val NORMAL: Int = 0
    private val FOOT: Int = 1
    private val LAST: Int = 2
    var isLastPage = false

    init {
        diff = mutableListOf()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NORMAL -> {
                val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_home_rv, parent, false)
                layout.findViewById<ImageView>(R.id.img_collect).visibility = View.GONE
                MyViewHolder(
                    layout
                )
            }

//            FOOT -> {
//                MyFootHolder(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.foot_rv, parent, false)
//                )
//            }

            else -> {
                MyLastHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.last_rv, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            if (isLastPage) {
                LAST
            } else {
                FOOT
            }
        } else {
            NORMAL
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("HomeRVAdapter", "onBindViewHolder: position = $position")
        if (getItemViewType(position) == NORMAL) {
            val data = diff[position]
            (holder as MyViewHolder).title.text = data.title
            holder.time.text = data.niceDate
            holder.type.text = data.superChapterName
            holder.tag1.visibility = if (data.fresh) View.VISIBLE else View.GONE
            holder.tag2.visibility = if (data.superChapterId == 408) View.VISIBLE else View.GONE
            holder.name.text = if (data.author.isEmpty()) data.shareUser else data.author

            if (data.collect) {
                holder.collect.setImageResource(R.drawable.icon_collect_2)
            } else {
                holder.collect.setImageResource(R.drawable.icon_collect_1)
            }


            holder.itemView.tag = position
            holder.itemView.setOnClickListener(this)

            holder.collect.tag = position
            holder.collect.setOnClickListener(this)
        }
    }

    override fun getItemCount(): Int {
        return if (diff.size == 0) 1 else diff.size + 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<ArticleDetailBean>) {
        //AsyncListDiffer需要一个新数据，不然添加无效
        diff.clear()
        diff.addAll(list)
        notifyDataSetChanged()
    }

    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var title: TextView = item.findViewById(R.id.title)
        var name: TextView = item.findViewById(R.id.name)
        var time: TextView = item.findViewById(R.id.time)
        var type: TextView = item.findViewById(R.id.type)
        var tag1: TextView = item.findViewById(R.id.tag1)
        var tag2: TextView = item.findViewById(R.id.tag2)
        var collect: ImageView = item.findViewById(R.id.img_collect)
    }

    class MyFootHolder(item: View) : RecyclerView.ViewHolder(item)

    class MyLastHolder(item: View) : RecyclerView.ViewHolder(item)

//    class MyCallback : DiffUtil.ItemCallback<a>() {
//        override fun areItemsTheSame(
//            oldItem: a,
//            newItem: a
//        ): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(
//            oldItem: a,
//            newItem: a
//        ): Boolean {
//            return oldItem.title == newItem.title && oldItem.niceDate == newItem.niceDate
//        }
//    }

    private var lastClickTime: Long = 0
    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > Constants.MIN_CLICK_DELAY_TIME && v != null) {
            lastClickTime = currentTime

            if (v.id == R.id.img_collect) {
                listener.onCollectClick(v.tag as Int)
            } else {
                listener.onItemClick(v.tag as Int)
            }

        }
    }
}