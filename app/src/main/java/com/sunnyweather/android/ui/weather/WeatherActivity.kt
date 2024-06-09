package com.sunnyweather.android.ui.weather

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 请求天气数据 将数据展示到界面上
 */
class WeatherActivity : AppCompatActivity() {
    /**
     * Creates a new instance of the Lazy that uses the specified initialization function initializer and the default thread-safety mode LazyThreadSafetyMode.SYNCHRONIZED.
     */
    val viewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }
    private lateinit var binding:ActivityWeatherBinding
    lateinit var drawerLayout:DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 实现 让背景图和状态栏融合到一起的效果
        val decorView = window.decorView
        decorView.systemUiVisibility=
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor=Color.TRANSPARENT
//        setContentView(R.layout.activity_weather)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        drawerLayout = binding.drawerLayout
        setContentView(binding.root)
        /**
         * 从Intent中取出经纬度坐标和地区名称
         */
        if (viewModel.locationLng.isEmpty())
        {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty())
        {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty())
        {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        /**
         * 对weatherLiveData对象进行观察
         */
        viewModel.weatherLiveData.observe(this){
            result->
            val weather = result.getOrNull()
            /**
             * 判断 是否获取到服务器返回的天气数据
             */
            if (weather!=null){
                showWeatherInfo(weather)
            }
            else{
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            // 表示刷新事件结束 隐藏刷新进度条
            binding.swipeRefreshLayout.isRefreshing=false
        }
        // 设置下拉刷新进度条的颜色
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        // 设置一个下拉刷新的监听器
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshWeather()
        }
        binding.nowLayout.navBtn.setOnClickListener {
            // 打开滑动菜单
            drawerLayout.openDrawer(GravityCompat.START)
        }
        // 监听drawerLayout的状态
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

            override fun onDrawerOpened(drawerView: View) = Unit

            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerClosed(drawerView: View) {
                // 隐藏输入法
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }

        })
    }

    /**
     * 刷新天气信息
     */
    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        // 显示下拉刷新进度条
        binding.swipeRefreshLayout.isRefreshing=true

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel("weather", "天气", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
        val notification = NotificationCompat
            .Builder(this, "weather")
            .setContentTitle(viewModel.placeName)
            .setContentText("天气更新中")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }

    /**
     * 逻辑：从Weather对象中获取数据，然后显示到相应的控件上
     */
    private fun showWeatherInfo(weather: Weather) {
        // 执行一次刷新天气的请求
        binding.nowLayout.placeName.text=viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        binding.nowLayout.currentTemp.text=currentTempText
        binding.nowLayout.currentSky.text= getSky(realtime.skycon).info
        val currentAQIText = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding.nowLayout.currentAQI.text= currentAQIText
        binding.nowLayout.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        val forecastLayout = binding.forecastLayout.forecastLayout
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days){
            // 处理每天的天气信息
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            // 动态加载R.layout.forecast_item布局并设置相应的数据，然后添加到父布局中
            val view =
                LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text=simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text=sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text=tempText
            forecastLayout.addView(view)
        }

        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        val lifeIndexLayout = binding.lifeIndexLayout
        lifeIndexLayout.coldRiskText.text = lifeIndex.coldRisk[0].desc
        lifeIndexLayout.dressingText.text = lifeIndex.dressing[0].desc
        lifeIndexLayout.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        lifeIndexLayout.carWashingText.text = lifeIndex.carWashing[0].desc
        // 让ScrollView变成可见状态
        binding.weatherLayout.visibility=View.VISIBLE
    }
}