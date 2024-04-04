package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers

/**
 * 单例类 仓库层
 * 主要工作：判断调用方请求的数据 应该是从本地获取还是从网络数据源中获取
 * 并将获得的数据返回给调用方
 */
object Repository {
    /**
     * 功能：搜索城市数据
     * Dispatchers.IO 子线程 进行网络请求
     */
    fun searchPlaces(query: String) = liveData(Dispatchers.IO){
        val result = try {
            val placeResponse =
                SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        // 通知数据的变化
        // Set's the LiveData's value to the given value
        emit(result)
    }
}