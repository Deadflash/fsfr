package com.fintrainer.fintrainer.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.ChapterRealm
import kotlinx.android.synthetic.main.item_chapters.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by krotk on 22.10.2017.
 */
class ChaptersAdapter(private val chapters: List<ChapterRealm>, private val clicked: OnChapterClick) : RecyclerView.Adapter<ChaptersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_chapters, parent, false))

    override fun getItemCount(): Int = chapters.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.tvChapter?.text = "Глава: ${position + 1}. ${chapters[position].chapterName ?: ""}"
        holder?.tvTestsCount?.text = chapters[position].testsCount.toString()
        holder?.chapterLayout?.onClick {
            clicked.onChapterSelected(position + 1)
        }
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val tvChapter = itemView?.tvChapter
        val tvTestsCount = itemView?.tvTestsCount
        val chapterLayout = itemView?.chapter_layout
    }

    interface OnChapterClick {
        fun onChapterSelected(position: Int)
    }
}