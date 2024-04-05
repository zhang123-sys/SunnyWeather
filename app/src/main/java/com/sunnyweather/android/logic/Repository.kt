package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

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
    fun searchPlaces(query: String) = fire(Dispatchers.IO){
            val placeResponse =
                SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
    }

    /**
     * 刷新天气信息
     */
    fun refreshWeather(lng:String, lat:String) = fire(Dispatchers.IO)
    {
        /**
         * Creates a CoroutineScope and calls the specified suspend block with this scope.
         */
        coroutineScope {
            /**
             * 并发执行
             */
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }

            /**
             * 同时得到响应结果后，才能进一步执行程序
             */
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather =
                    Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                // 包装异常信息
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}\n" +
                                "daily response.status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    /**
     * 按照liveData()函数的参数接收标准定义的一个高阶函数
     * 简化 try catch 异常捕获语句
     */
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place:Place) = PlaceDao.savePlace(place = place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}