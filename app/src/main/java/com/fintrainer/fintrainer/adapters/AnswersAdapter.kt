package com.fintrainer.fintrainer.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.AnswersDto
import kotlinx.android.synthetic.main.item_answers.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by krotk on 22.10.2017.
 */
class AnswersAdapter(private val iAnswers: IAnswers, private var answers: List<AnswersDto>) : RecyclerView.Adapter<AnswersAdapter.ViewHolder>() {

    override fun getItemCount(): Int = answers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_answers, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAnswer?.text = answers[position].text
        holder.tvAnswerPosition?.text = (position +1).toString()
//        holder.answerLayout?.setBackgroundResource(R.drawable.ripple_main)
        holder.answerLayout?.onClick { iAnswers.onAnswerClicked(position, false) }
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val tvAnswerPosition = itemView?.tvAnswerPosition
        val tvAnswer = itemView?.tvAnswer
        val answerLayout = itemView?.answerLayout
    }

    interface IAnswers {
        fun onAnswerClicked(position: Int, isRight: Boolean)
    }
}