package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.OnItemClickListener
import com.example.myapplication.R
import kotlinx.android.synthetic.main.item_scrolling.view.*

class ScrollingAdapter(private val context: Context, private val list: List<String>) : RecyclerView.Adapter<ScrollingAdapter.FirstViewHolder>() {
    var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirstViewHolder {
        return FirstViewHolder(LayoutInflater.from(context).inflate(R.layout.item_scrolling, parent, false))
    }

    override fun onBindViewHolder(holder: FirstViewHolder, position: Int) {
        holder.itemTv.text = list[position]
        holder.itemTv.setOnClickListener {
            listener?.onItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class FirstViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTv: TextView = view.itemTv
    }
}