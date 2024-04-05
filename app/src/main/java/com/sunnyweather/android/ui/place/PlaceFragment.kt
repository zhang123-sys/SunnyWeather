package com.sunnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.ui.weather.WeatherActivity

class PlaceFragment: Fragment() {
    /**
     * 懒加载
     */
    val viewModel:PlaceViewModel by lazy {
        ViewModelProvider(this).get(PlaceViewModel::class.java)
    }
    private lateinit var adapter: PlaceAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }
/*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * 判断是否已经有存储的城市数据
         */
        // 当PlaceFragment被嵌入MainActivity中，并且之前已经存在选中的城市
        if (activity is MainActivity && viewModel.isPlaceSaved())
        {
            // 获取已存储的数据
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                val location = place.location
                putExtra("location_lng", location.lng)
                putExtra("location_lat", location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val bgImageView = view.findViewById<ImageView>(R.id.bgImageView)
        val layoutManager = LinearLayoutManager(activity)
        // 设置 layoutManager
        recyclerView.layoutManager=layoutManager
        adapter= PlaceAdapter(this, placeList = viewModel.placeList)
        // 设置 适配器
        recyclerView.adapter=adapter
        val searchPlaceEdit = view.findViewById<EditText>(R.id.searchPlaceEdit)
        // 监听搜索框内容的变化情况
        searchPlaceEdit.addTextChangedListener {
            editable: Editable? ->
            val content = editable.toString()
            if (content.isNotEmpty())
            {
                // 发起搜索城市数据的网络请求了
                viewModel.searchPlaces(content)
            }
            else
            {
                // 隐藏recyclerView
                recyclerView.visibility=View.GONE
                bgImageView.visibility=View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner){
            result ->
            val places = result.getOrNull()
            if (places!=null)
            {
                recyclerView.visibility=View.VISIBLE
                bgImageView.visibility=View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                // 通知PlaceAdapter刷新界面
                adapter.notifyDataSetChanged()
            }
            else
            {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                // 打印具体的异常原因
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
}