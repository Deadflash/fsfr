package com.fintrainer.fintrainer.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.utils.Constants.EXAM_INTENT
import kotlinx.android.synthetic.main.item_tests_tabbar.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by deadf on 02.11.2017.
 */
class TestingTabbarAdapter(private val tests: List<TestingDto>, private val onItemClick: OnItemClick, private val intentId: Int) : RecyclerView.Adapter<TestingTabbarAdapter.ViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tests_tabbar, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTabNumber?.text = (position + 1).toString()

        holder.tabBarLayout?.background = ContextCompat.getDrawable(holder.itemView?.context!!, R.color.blue_grey_50)

        if (tests[position].clicked == true) {
            if (intentId != EXAM_INTENT && intentId != -1) {
                if (tests[position].rightAnswer == true) {
                    if (selectedPosition == position) {
                        holder.tabBarLayout?.background = ContextCompat.getDrawable(holder.itemView.context!!, R.color.green_300_transparent)
                    } else {
                        holder.tabBarLayout?.background = ContextCompat.getDrawable(holder.itemView.context!!, R.color.green_300)
                    }
                } else {
                    if (selectedPosition == position) {
                        holder.tabBarLayout?.background = ContextCompat.getDrawable(holder.itemView.context!!, R.color.red_300_transparent)
                    } else {
                        holder.tabBarLayout?.background = ContextCompat.getDrawable(holder.itemView.context!!, R.color.red_300)
                    }
                }
            } else {
                if (selectedPosition == position) {
                    holder.tabBarLayout?.background = ContextCompat.getDrawable(holder.itemView.context!!, R.color.blue_grey_300_transparent)
                } else {
                    holder.tabBarLayout?.background = ContextCompat.getDrawable(holder.itemView.context!!, R.color.blue_grey_300)
                }
            }
        } else {
            if (selectedPosition == position) {
                holder.tabBarLayout?.background = ContextCompat.getDrawable(holder.itemView.context!!, R.color.blue_grey_300_transparent)
            }
        }

        holder.tabBarLayout?.onClick { onItemClick.onTabClicked(position) }
    }

    fun setSelectedPosition(position: Int) {
        this.selectedPosition = position
    }

    override fun getItemCount(): Int = tests.count()

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val tvTabNumber = itemView?.tvTabNumber
        val tabBarLayout = itemView?.tab_bar_layout
    }

    interface OnItemClick {
        fun onTabClicked(position: Int)
    }
}