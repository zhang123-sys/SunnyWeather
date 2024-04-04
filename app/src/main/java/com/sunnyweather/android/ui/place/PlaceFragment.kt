package com.sunnyweather.android.ui.place

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
import com.sunnyweather.android.R

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
                adapter.notifyDataSetChanged()
            }
            else
            {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
}