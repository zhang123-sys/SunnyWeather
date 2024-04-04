package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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

    fun refreshWeather(lng:String, lat:String) = liveData(Dispatchers.IO)
    {
        val result = try {
            /**
             * 创建协程作用域
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
        } catch (e: Exception) {
            Result.failure<Weather>(e)
        }
        // 将包装的结果发射出去
        emit(result)
    }
}