package com.sunnyweather.android.logic.model

/**
 * 封装Realtime和Daily对象
 */
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)