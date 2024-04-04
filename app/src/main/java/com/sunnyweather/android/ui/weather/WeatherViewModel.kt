package com.sunnyweather.android.ui.weather

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Location

@SuppressLint("CheckResult")
class WeatherViewModel:ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()

    /**
     * 和界面相关的数据
     */
    var locationLng = ""
    var locationLat = ""
    var placeName = ""

    /**
     * 观察对象 使用转换函数
     */
    val weatherLiveData = locationLiveData.switchMap { location ->
        Repository.refreshWeather(lng = location.lng, lat = location.lat)
    }

    /**
     * 刷新天气信息
     */
    fun refreshWeather(lng:String, lat:String){
        locationLiveData.value= Location(lng, lat)
    }
}