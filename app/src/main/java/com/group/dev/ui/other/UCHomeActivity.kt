package com.group.dev.ui.other

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.group.dev.R
import com.group.dev.api.ApiService
import com.group.common.base.BaseActivity
import com.group.dev.data.PageData
import com.group.dev.error
import com.group.dev.requestComplete
import com.group.dev.smoothToTop
import com.group.dev.ui.wanandroid.ArticleItem
import com.group.dev.widget.behavior.UCViewHeaderBehaviorNormal
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.tencent.mmkv.MMKV
import com.group.common.adapter.ItemCell
import com.group.common.adapter.RecyclerAdapter
import com.group.common.adapter.RecyclerSupport
import kotlinx.android.synthetic.main.activity_new_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 描述:仿uc首页
 *
 * author zhaoys
 * create by 2019/7/29 0029
 */
class UCHomeActivity : BaseActivity() {

    private lateinit var mainAdapter: RecyclerAdapter
    private val support = RecyclerSupport()
    private lateinit var headerBehavior: UCViewHeaderBehaviorNormal
    private var index = 0

    override fun layoutId(): Int = R.layout.activity_new_home

    override fun init(savedInstanceState: Bundle?) {
        MMKV.initialize(this)
        mainAdapter = RecyclerAdapter(support)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@UCHomeActivity)
            adapter = mainAdapter
        }
        articleList(0)
        val behavior =
            (id_uc_view_header_layout.layoutParams as CoordinatorLayout.LayoutParams).behavior
        headerBehavior = behavior as UCViewHeaderBehaviorNormal
        smartRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                index++
                articleList(index)
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                index = 0
                articleList(0)
            }
        })
    }

    private fun articleList(index: Int) {
        GlobalScope.launch {
            try {
                val response = ApiService.testApi.articleList(index)
                if (response.errorCode == 0) {
                    val list = mutableListOf<ItemCell>()
                    val temp = response.data?.datas ?: emptyList()
                    val size = temp.size
                    for (i in 0 until size) {
                        list.add(ArticleItem(0, temp[i]))
                    }
                    GlobalScope.launch(context = Dispatchers.Main, block = {
                        mainAdapter.submit(list)
                        val pageData = response.data?.let {
                            PageData(it.curPage, it.size, it.total)
                        }
                        smartRefreshLayout.requestComplete(pageData)
                    })
                } else {
                    smartRefreshLayout.error(index + 1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                smartRefreshLayout.error(index + 1)
            }
        }
    }

    override fun onBackPressed() {
        if (headerBehavior.isClosed) {
            headerBehavior.openPager()
            recyclerView.smoothToTop()
        } else {
            super.onBackPressed()
        }
    }
}