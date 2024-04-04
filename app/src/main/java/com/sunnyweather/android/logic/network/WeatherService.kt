package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {
    /**
     * 获取实时的天气信息
     */
    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng:String, @Path("lat") lat:String):
            Call<RealtimeResponse>
    // 使用了@Path注解来向请求接口中动态传入经纬度的坐标

    /**
     * 获取未来的天气信息
     */
    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng:String, @Path("lat") lat:String):
            Call<DailyResponse>
}