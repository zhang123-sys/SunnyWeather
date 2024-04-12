package com.sunnyweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.ui.weather.WeatherActivity

/**
 * RecyclerView的适配器
 */
class PlaceAdapter(private val fragment:PlaceFragment, private val placeList: List<Place>): RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view) {
        val placeName = view.findViewById<TextView>(R.id.placeName)
        val placeAddress = view.findViewById<TextView>(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        val holder = ViewHolder(view)
        // 给最外层布局 注册 点击事件监听器
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            val activity = fragment.activity
            /**
             * 如果是在WeatherActivity中
             */
            if (activity is WeatherActivity)
            {
                // 不需要跳转，只要去请求新选择城市的天气信息
                // 关闭滑动菜单
                val weatherActivity = activity as WeatherActivity
                weatherActivity.drawerLayout.closeDrawers()
                weatherActivity.viewModel.locationLng=place.location.lng
                weatherActivity.viewModel.locationLat=place.location.lat
                weatherActivity.viewModel.placeName=place.name
                weatherActivity.refreshWeather()
            }
            else
            {
                // 如果是在MainActivity中
                // 跳转界面
                val intent = Intent(parent.context, WeatherActivity::class.java).
                apply {
                    putExtra("location_lng", place.location.lng)
                    putExtra("location_lat", place.location.lat)
                    putExtra("place_name", place.name)
                }
                fragment.startActivity(intent)
                fragment.activity?.finish()
            }
            // 存储选中的城市
            fragment.viewModel.savePlace(place)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text=place.name
        holder.placeAddress.text=place.address
    }

    override fun getItemCount() = placeList.size
}