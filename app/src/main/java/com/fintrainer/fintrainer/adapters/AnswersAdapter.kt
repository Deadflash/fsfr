package com.fintrainer.fintrainer.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.utils.Constants.CHAPTER_INTENT
import com.fintrainer.fintrainer.utils.Constants.EXAM_INTENT
import com.fintrainer.fintrainer.utils.Constants.FAILED_TESTS_INTENT
import com.fintrainer.fintrainer.utils.Constants.FAVOURITE_INTENT
import com.fintrainer.fintrainer.utils.Constants.SEARCH_INTENT
import com.fintrainer.fintrainer.utils.Constants.TESTING_INTENT
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
        if (test.clicked == true || intentId == SEARCH_INTENT) {
            if (test.answers?.get(position)?.clicked == true) {
                setAnswerLayoutBg(position, holder)
            } else if (intentId == FAILED_TESTS_INTENT || intentId == CHAPTER_INTENT || intentId == TESTING_INTENT || intentId == SEARCH_INTENT && test.answers?.get(position)?.status == true) {
                setAnswerLayoutBg(position, holder)
            }
        }
        holder.answerLayout?.onClick {
            if (intentId != FAILED_TESTS_INTENT && intentId != SEARCH_INTENT) {
                if (test.clicked != true) {
                    test.clicked = true
                    test.answers?.get(position)?.clicked = true
                    if (intentId == TESTING_INTENT || intentId == CHAPTER_INTENT || intentId == FAVOURITE_INTENT) {
                        notifyDataSetChanged()
                    } else {
                        setAnswerLayoutBg(position, holder)
                    }
                    iAnswers.addTestProgress(test.answers?.get(position)?.status ?: false, test.weight ?: 0, test.chapter ?: 0)
                    if (intentId == EXAM_INTENT || intentId == -1) {
                        iAnswers.onAnswerClicked()
                    }
                } else {
                    iAnswers.onAnswerClicked()
                }
            }
        }
    }

    private fun setAnswerLayoutBg(position: Int, holder: ViewHolder) {
        if (intentId == EXAM_INTENT || intentId == -1) {
            holder.answerLayout?.setBackgroundResource(R.drawable.ripple_main)
        } else {
            if (test.answers?.get(position)?.status == true || test.answers?.get(position)?.clicked == true) {
                if (test.answers?.get(position)?.status == true) {
                    holder.answerLayout?.setBackgroundResource(R.drawable.ripple_right)
                    holder.ivCircle?.setBackgroundResource(R.drawable.answer_circle_right)
                    holder.tvAnswerPosition?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green_500))
                } else {
                    holder.answerLayout?.setBackgroundResource(R.drawable.ripple_wrong)
                    holder.ivCircle?.setBackgroundResource(R.drawable.answer_circle_wrong)
                    holder.tvAnswerPosition?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red_500))
                }
            }
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