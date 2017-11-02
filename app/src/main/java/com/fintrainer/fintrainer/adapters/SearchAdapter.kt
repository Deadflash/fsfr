package com.fintrainer.fintrainer.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.TestingDto
import kotlinx.android.synthetic.main.item_search.view.*

/**
 * Created by krotk on 23.10.2017.
 */
class SearchAdapter(private val intentId: Int, private val questions: List<TestingDto>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>(), Filterable {

    private val itemFilter: ItemFilter = ItemFilter()
    private var filteredQuestions: List<TestingDto> = questions

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.recycler?.layoutManager = LinearLayoutManager(holder?.itemView?.context)
        holder?.recycler?.adapter = AnswersAdapter(object : AnswersAdapter.IAnswers {
            override fun refreshTabLayout() {

            }

            override fun onAnswerClicked() {}

            override fun addTestProgress(isRight: Boolean, weight: Int, chapter: Int) {}
        }, filteredQuestions[position], intentId)

        holder?.tvQuestion?.text = filteredQuestions[position].task ?: ""
        holder?.tvQuestionCode?.text = filteredQuestions[position].code ?: ""

//        holder?.ivQuestionImage
    }

    override fun getItemCount(): Int = filteredQuestions.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_search, parent, false))

    override fun getFilter(): Filter = itemFilter

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val recycler = itemView?.searchAnswersRecycler
        val tvQuestion = itemView?.tvQuestion
        val tvQuestionCode = itemView?.tvQuestionCode
        val ivQuestionImage = itemView?.ivQuestionImg
    }

    private inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {

            val filterString = constraint.toString().toLowerCase()
            val results = Filter.FilterResults()
            val list = questions
            val count = list.size
            val nlist = ArrayList<TestingDto>(count)
            var filterableString: TestingDto

            for (i in 0 until count) {
                filterableString = list[i]
                if (filterableString.task != null) {
                    if (filterableString.task!!.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString)
                    }
                }
            }

            results.values = nlist
            results.count = nlist.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            filteredQuestions = results.values as List<TestingDto>
            notifyDataSetChanged()
        }

    }
}