package com.sunnyweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place

object PlaceDao {
    private const val PLACE = "place"

    /**
     * 将Place对象存储到SharedPreferences文件中
     */
    fun savePlace(place: Place)
    {
        sharedPreferences().edit {
            putString(PLACE, Gson().toJson(place))
        }
    }

    /**
     * 读取Place对象
     */
    fun getSavedPlace():Place
    {
        val placeJson = sharedPreferences().getString(PLACE, "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    /**
     * 判断是否有数据已被存储
     */
    fun isPlaceSaved() = sharedPreferences().contains(PLACE)

    private fun sharedPreferences() = SunnyWeatherApplication.context.
    getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}