package com.fintrainer.fintrainer.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.DiscussionCommentDto
import com.fintrainer.fintrainer.utils.Constants.DISLIKES_TO_HIDE_COMMENT
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.fragment_discussion_comment_item.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by deadf on 31.10.2017.
 */
class CommentsAdapter(private val account: GoogleSignInAccount?, private val comments: List<DiscussionCommentDto>, private val onRateClick: OnRateClick) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var superRate = 0

        holder?.userName?.text = comments[position].commentCreator ?: ""
        holder?.comment?.text = comments[position].text ?: ""

        comments[position].rateList?.forEach {
            it?.let { comment ->
                comment.direction?.let {
                    superRate = if (it) superRate.plus(1) else superRate.minus(1)

                    if (account?.email == comment.userId) {
                        if (it) {
                            holder?.like?.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.green_300))
                            holder?.dislike?.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.blue_grey_300))
                        } else {
                            holder?.dislike?.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red_300))
                            holder?.like?.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.blue_grey_300))
                        }
                    }
                }
            }
        }

        holder?.like?.onClick {
            onRateClick.onClick(comments[position],true)
        }
        holder?.dislike?.onClick {
            onRateClick.onClick(comments[position],false)
        }

        if (superRate < DISLIKES_TO_HIDE_COMMENT) {
            holder?.mainLayout?.visibility = View.GONE
            holder?.negativeLayout?.visibility = View.VISIBLE
        } else {
            holder?.mainLayout?.visibility = View.VISIBLE
            holder?.negativeLayout?.visibility = View.GONE
        }

        holder?.rateCount?.text = superRate.toString()
    }

    override fun getItemCount(): Int = comments.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent?.context)
            .inflate(R.layout.fragment_discussion_comment_item, parent, false))

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView?.tvUserName
        val comment = itemView?.discussionCommentText
        val rateCount = itemView?.rate_view
        val like = itemView?.rate_btn
        val dislike = itemView?.unrate_btn
        val showNegative = itemView?.tvNegativeShowBtn
        val negativeLayout = itemView?.discussion_negative_layout
        val mainLayout = itemView?.discussion_main_layout
    }

    interface OnRateClick {
        fun onClick(comment: DiscussionCommentDto, rate: Boolean)
    }
}