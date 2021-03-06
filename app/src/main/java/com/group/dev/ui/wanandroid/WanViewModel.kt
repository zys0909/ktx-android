package com.group.dev.ui.wanandroid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group.dev.api.ApiService
import com.group.dev.data.response.ArticleData
import com.group.dev.data.response.HomeBannerData
import kotlinx.coroutines.launch

class WanViewModel : ViewModel() {
    private val bannerLiveData by lazy {
        MutableLiveData<List<HomeBannerData>>()
    }
    private val articleLiveData by lazy {
        MutableLiveData<List<ArticleData>>()
    }

    /**
     * 首页Banner
     */
    fun banner(): LiveData<List<HomeBannerData>> {
        if (bannerLiveData.value == null) {
            bannerRequest()
        }
        return bannerLiveData
    }

    private fun bannerRequest() {
        viewModelScope.launch {
            try {
                val await = ApiService.testApi.homeBanner()
                if (await.errorCode == 0) {
                    bannerLiveData.postValue(await.data)
                } else {
                    bannerLiveData.postValue(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 置顶文章
     */
    fun articleTop(): LiveData<List<ArticleData>> {
        if (articleLiveData.value == null) {
            articleTopRequest()
        }
        return articleLiveData
    }

    private fun articleTopRequest() {
        viewModelScope.launch {
            try {
                val await = ApiService.testApi.articleTop()
                if (await.errorCode == 0) {
                    articleLiveData.postValue(await.data)
                } else {
                    articleLiveData.postValue(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

}