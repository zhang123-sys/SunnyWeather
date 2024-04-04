package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

data class RealtimeResponse(val status:String, val result: Result){
    // 定义在内部 防止类名有同名冲突
    data class Result(val realtime: Realtime)

    data class Realtime(val temperature: Float, val skycon:String,
                        @SerializedName("air_quality") val airQuality:AirQuality)

    data class AirQuality(val aqi:AQI)

    data class AQI(val chn:Float)
}


