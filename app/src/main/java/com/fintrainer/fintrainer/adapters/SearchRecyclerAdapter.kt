package com.fintrainer.fintrainer.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R
import kotlinx.android.synthetic.main.item_tests_tabbar.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor

/**
 * Created by deadf on 18.02.2018.
 */
class SearchRecyclerAdapter(private val chapters: Int, private val context: Context, private val onitemclickListener: OnItemClickListener) : RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tests_tabbar, parent, false))

    override fun getItemCount(): Int = chapters

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.tvTabbar?.text = (position + 1).toString()
        holder?.tvTabbar?.textColor = ContextCompat.getColor(context, R.color.blue_grey_50)

        holder?.tabBarLayout?.onClick { onitemclickListener.onClick(position) }
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val tvTabbar = itemView?.tvTabNumber
        val tabBarLayout = itemView?.tab_bar_layout
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }
}