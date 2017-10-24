package com.fintrainer.fintrainer.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R
import kotlinx.android.synthetic.main.item_search.view.*

/**
 * Created by krotk on 23.10.2017.
 */
class SearchAdapter: RecyclerView.Adapter<SearchAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.recycler?.layoutManager = LinearLayoutManager(holder?.itemView?.context)
        holder?.recycler?.adapter = AnswersAdapter(object : AnswersAdapter.IAnswers{
            override fun onAnswerClicked(position: Int, isRight: Boolean) {

            }
        }, emptyList())
    }

    override fun getItemCount(): Int = 50

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_search,parent,false))

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val recycler = itemView?.searchAnswersRecycler
    }
}