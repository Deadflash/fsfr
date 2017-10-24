package com.fintrainer.fintrainer.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.utils.Constants.EXAM_INTENT
import kotlinx.android.synthetic.main.item_answers.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by krotk on 22.10.2017.
 */
class AnswersAdapter(private val iAnswers: IAnswers, private var test: TestingDto, private val intentId: Int) : RecyclerView.Adapter<AnswersAdapter.ViewHolder>() {

    override fun getItemCount(): Int = test.answers?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_answers, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAnswer?.text = test.answers?.get(position)?.text ?: ""
        holder.tvAnswerPosition?.text = (position + 1).toString()
        if (test.clicked == true) {
            if (test.answers?.get(position)?.clicked == true) {
                setAnswerLayoutBg(position, holder)
            }
        }
        holder.answerLayout?.onClick {
            if (test.clicked != true) {
                test.clicked = true
                test.answers?.get(position)?.clicked = true
                setAnswerLayoutBg(position, holder)
                iAnswers.addTestProgress(test.answers?.get(position)?.status == true, test.weight ?: 0, test.chapter ?: 0)
                if (intentId == EXAM_INTENT || intentId == -1) {
                    iAnswers.onAnswerClicked()
                }
            } else {
                iAnswers.onAnswerClicked()
            }
        }
    }

    private fun setAnswerLayoutBg(position: Int, holder: ViewHolder) {
        if (intentId == EXAM_INTENT || intentId == -1) {
            holder.answerLayout?.setBackgroundResource(R.drawable.answer_layout)
        } else {
            val resource = if (test.answers?.get(position)?.status == true) R.drawable.answer_circle_right else R.drawable.answer_circle_wrong
            holder.answerLayout?.setBackgroundResource(resource)
            holder.ivCircle?.setBackgroundColor(resource)
        }
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val tvAnswerPosition = itemView?.tvAnswerPosition
        val tvAnswer = itemView?.tvAnswer
        val answerLayout = itemView?.answerLayout
        val ivCircle = itemView?.ivAnswerCircle
    }

    interface IAnswers {
        fun onAnswerClicked()
        fun addTestProgress(isRight: Boolean, weight: Int, chapter: Int)
    }
}