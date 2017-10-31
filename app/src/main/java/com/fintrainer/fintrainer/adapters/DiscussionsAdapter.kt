package com.fintrainer.fintrainer.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.DiscussionQuestionDto
import com.fintrainer.fintrainer.utils.Constants.DISLIKES_TO_HIDE_COMMENT
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.item_discussions.view.*

/**
 * Created by krotk on 23.10.2017.
 */
class DiscussionsAdapter(val discussions: List<DiscussionQuestionDto>,val account: GoogleSignInAccount?) : RecyclerView.Adapter<DiscussionsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_discussions,parent,false))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var superRate: Int = 0

        holder?.userName?.text = discussions[position].discussionCreator ?: ""
        holder?.discussionText?.text = discussions[position].text ?: ""
        holder?.tvCommentsCount?.text = discussions[position].commentList?.size.toString()
        holder?.dislike?.setColorFilter(ContextCompat.getColor(holder.itemView?.context, R.color.blue_grey_300))
        holder?.likes?.setColorFilter(ContextCompat.getColor(holder.itemView?.context, R.color.blue_grey_300))

        discussions[position].rateList?.let {
            for (rateElem in it){
                superRate = if (rateElem.direction!!) superRate.plus(1) else superRate.minus(1)
                if (account?.email.equals(rateElem.userId)) {
                    if (rateElem.direction!!) {
                        holder?.likes?.setColorFilter(ContextCompat.getColor(holder.itemView?.context, R.color.green_300))
                        holder?.dislike?.setColorFilter(ContextCompat.getColor(holder.itemView?.context, R.color.blue_grey_300))
                    } else {
                        holder?.dislike?.setColorFilter(ContextCompat.getColor(holder.itemView?.context, R.color.red_300))
                        holder?.likes?.setColorFilter(ContextCompat.getColor(holder.itemView?.context, R.color.blue_grey_300))
                    }
                }
            }
        }

        holder?.rateCount?.text = superRate.toString()
        if (superRate < DISLIKES_TO_HIDE_COMMENT) {
            holder?.mainLayout?.visibility = View.GONE
            holder?.negativeLayout?.visibility = View.VISIBLE
        } else {
            holder?.mainLayout?.visibility = View.VISIBLE
            holder?.negativeLayout?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = discussions.size

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView?.tvUserName
        val discussionText = itemView?.discussionText
        val ivStartDiscussion = itemView?.ivStartDiscussion
        val tvCommentsCount = itemView?.tvCommentsCount
        val likes = itemView?.rate_btn
        val dislike = itemView?.unrate_btn
        val rateCount = itemView?.rate_view

        val mainLayout = itemView?.discussion_main_layout
        val negativeLayout = itemView?.discussion_negative_layout
    }
}