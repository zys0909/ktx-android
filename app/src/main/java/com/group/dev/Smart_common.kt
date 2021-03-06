package com.group.dev

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group.dev.data.PageData
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.group.common.ext.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 描述:SmartRefreshLayout
 *
 * author zhaoys
 * create by 2019/7/24 0024
 */
fun SmartRefreshLayout.error(index: Int) {
    GlobalScope.launch(context = Dispatchers.Main, block = {
        if (index == 1) finishRefresh() else finishLoadMore()
        context.applicationContext.toast("网络开小差啦!")
    })
}

fun SmartRefreshLayout.requestComplete(page: PageData?, recyclerView: RecyclerView? = null) {
    if (page == null) {
        return
    }
    val noData = page.curPage * page.size >= page.total
    when (page.curPage) {
        1 -> {
            recyclerView?.let {
                val layoutManager = recyclerView.layoutManager
                if (layoutManager is LinearLayoutManager) {
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (position > -1) {
                        layoutManager.scrollToPosition(0)
                    }
                }
            }
            finishRefresh( false)
            setNoMoreData(noData)
        }
        else -> {
            if (noData) {
                finishLoadMoreWithNoMoreData()
            } else {
                finishLoadMore()
            }
        }
    }
}

internal fun RecyclerView.smoothToTop() {
    val manager = this.layoutManager as? LinearLayoutManager ?: return
    if (this.scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
        this.stopScroll()
        return
    }
    val position = manager.findFirstCompletelyVisibleItemPosition()
    if (position > -1) {
        if (position > 10) {
            this.scrollToPosition(5)
        }
        this.smoothScrollToPosition(0)
    }
}