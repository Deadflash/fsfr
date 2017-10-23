package com.fintrainer.fintrainer.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R

/**
 * Created by krotk on 23.10.2017.
 */
class DiscussionsAdapter : RecyclerView.Adapter<DiscussionsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_discussions,parent,false))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

    }

    override fun getItemCount(): Int = 7

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    }
}