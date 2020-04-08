package com.chunyingyen.weather.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chunyingyen.weather.IITemClickListener
import com.chunyingyen.weather.R
import com.chunyingyen.weather.data.Hours36TemperatureData

class Hours36ListAdapter(private val listData: MutableList<Hours36TemperatureData>, private val listener: IITemClickListener): RecyclerView.Adapter<Hours36ListAdapter.ItemViewHolder>() {

    private val TAG = this::class.java.simpleName

    inner class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val img by lazy { itemView.findViewById<ImageView>(R.id.info_img) }
        val txt by lazy { itemView.findViewById<TextView>(R.id.info_txt) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_list, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.txt.text =
            String.format(
                "%s\n%s\n%s\n%s",
                listData[position].startTime,
                listData[position].endTime,
                listData[position].name,
                listData[position].unit)
        holder.txt.setOnClickListener { listener.onClick(listData[position]) }
        holder.img.setImageResource(R.drawable.ic_launcher_background)

    }
}