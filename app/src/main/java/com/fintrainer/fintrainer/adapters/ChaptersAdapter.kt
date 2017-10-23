package com.fintrainer.fintrainer.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R
import kotlinx.android.synthetic.main.item_chapters.view.*

/**
 * Created by krotk on 22.10.2017.
 */
class ChaptersAdapter : RecyclerView.Adapter<ChaptersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_chapters, parent, false))

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.tvChapter?.text = "Глава номер 1"
        holder?.tvTestsCount?.text = "351"
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val tvChapter = itemView?.tvChapter
        val tvTestsCount = itemView?.tvTestsCount
    }
}