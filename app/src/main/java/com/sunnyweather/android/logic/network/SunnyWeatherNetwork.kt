package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 网络数据源访问入口
 */
object SunnyWeatherNetwork {
    /**
     * 创建动态代理对象
     */
    private val weatherService =
//        ServiceCreator.create(WeatherService::class.java)
        ServiceCreator.create<WeatherService>()

    /**
     * 挂起函数
     */
    suspend fun getDailyWeather(lng: String, lat: String) =
        weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng:String, lat: String) =
        weatherService.getRealtimeWeather(lng = lng, lat = lat).await()

    /**
     * 创建动态代理对象
     */
    private val placeService=
        ServiceCreator.create<PlaceService>()

    /**
     * 挂起函数
     */
    suspend fun searchPlaces(query: String) =
        placeService.searchPlaces(query).await()

    /**
     * 借助协程技术实现
     */
    private suspend fun <T> Call<T>.await():T{
        // suspendCoroutine 挂起当前协程
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body!=null)
                    {
                        continuation.resume(body)
                    }
                    else
                    {
                        continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }

}