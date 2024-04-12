package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place

class PlaceViewModel: ViewModel() {
    private val searchLiveData=MutableLiveData<String>()

    /**
     * 集合 用于对界面上显示的城市数据进行缓存
     */
    val placeList = ArrayList<Place>()

    /**
     * 调用 转换函数
     * 观察仓库层返回的searchLiveData对象
     */
    val placeLiveData = searchLiveData.switchMap { query ->
        // 发起网络请求
        Repository.searchPlaces(query)
    }

    /**
     * 触发转换函数执行
     */
    fun searchPlaces(query: String)
    {
        searchLiveData.value=query
    }

    /**
     * 调用仓库层相应的接口
     */
    fun savePlace(place:Place) = Repository.savePlace(place = place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()
}