package com.fintrainer.fintrainer.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.utils.containers.PicassoContainer
import com.fintrainer.fintrainer.utils.header.SectioningAdapter
import kotlinx.android.synthetic.main.item_search.view.*
import kotlinx.android.synthetic.main.search_header_layout.view.*

/**
 * Created by krotk on 23.10.2017.
 */
class SearchAdapter(private val intentId: Int, private val questions: List<TestingDto>, private val picasso: PicassoContainer, private val context: Context, private val fabVisibilityController: FabVisibilityController)
    : SectioningAdapter(), Filterable {

    private var sections = mutableListOf<Section>()
    private val itemFilter: ItemFilter = ItemFilter()
    private var filteredQuestions: List<TestingDto> = fillFilteredQuestions(questions)
    private var sectionQuestions = mutableListOf<TestingDto>()
    private var wrongAnswersVisibleState: Boolean = false

    class Section {
        var header: Int? = null
        var tests = mutableListOf<TestingDto>()
    }

    fun changeWrongAnswersVisible(newState: Boolean) {
        wrongAnswersVisibleState = newState
    }

    override fun getNumberOfSections(): Int = sections.size

    override fun doesSectionHaveFooter(sectionIndex: Int): Boolean = false

    override fun doesSectionHaveHeader(sectionIndex: Int): Boolean = true

    override fun getNumberOfItemsInSection(sectionIndex: Int): Int = sections[sectionIndex].tests.size

    private fun fillFilteredQuestions(questions: List<TestingDto>): List<TestingDto> {
        sections.clear()
        filteredQuestions = questions
        filteredQuestions.sortedWith(compareBy({ it.chapter }))

        var chapter = 1
        var currentSection: Section? = null
        filteredQuestions.forEach { testingDto: TestingDto ->
            if (testingDto.chapter == chapter) {
                if (currentSection == null) {
                    currentSection = Section()
                    sections.add(currentSection!!)
                }
                currentSection?.header = testingDto.chapter
                currentSection?.tests?.add(testingDto)
            } else {
                chapter = testingDto.chapter!!
                currentSection = Section()
                sections.add(currentSection!!)
                currentSection?.header = testingDto.chapter
                currentSection?.tests?.add(testingDto)
            }
        }
        return filteredQuestions
    }

    override fun onCreateItemViewHolder(parent: ViewGroup?, itemUserType: Int): SectioningAdapter.ItemViewHolder? =
            ItemViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_search, parent, false))

    override fun onCreateHeaderViewHolder(parent: ViewGroup?, headerUserType: Int): SectioningAdapter.HeaderViewHolder? =
            HeaderViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.search_header_layout, parent, false))

    override fun onBindHeaderViewHolder(holder: SectioningAdapter.HeaderViewHolder?, sectionIndex: Int, headerUserType: Int) {
        holder as HeaderViewHolder
        if (sections.isEmpty()) {
            holder.headerLayout?.visibility = View.GONE
        }
        holder.tvHeader?.text = "Глава ${sections[sectionIndex].tests[0].chapter}"
    }

    override fun onBindItemViewHolder(holder: SectioningAdapter.ItemViewHolder, sectionIndex: Int, position: Int, itemUserType: Int) {
        holder as ItemViewHolder

        holder.recycler?.layoutManager = object : LinearLayoutManager(holder.itemView?.context) {
            override fun canScrollVertically(): Boolean = false
        }
        holder.recycler?.isNestedScrollingEnabled = false
        holder.recycler?.adapter = AnswersAdapter(object : AnswersAdapter.IAnswers {
            override fun autoRemoveFromFavourite() {}

            override fun autoAddToFavourite() {}

            override fun addChapterStatistics(index: Int, code: String, chapter: Int, clickedAnswer: Int) {}

            override fun refreshTabLayout() {}

            override fun onAnswerClicked() {}

            override fun addTestProgress(isRight: Boolean, weight: Int, chapter: Int) {}
        }, sections[sectionIndex].tests[position], intentId)
        (holder.recycler?.adapter as AnswersAdapter).changeWrongAnswersVisible(wrongAnswersVisibleState)



        holder.tvQuestion?.text = sections[sectionIndex].tests[position].task ?: ""
        holder.tvQuestionCode?.text = sections[sectionIndex].tests[position].code ?: ""
        holder.ivQuestionImage?.visibility = View.GONE

        sections[sectionIndex].tests[position].image?.let {
            context.resources?.getIdentifier(it, "drawable", context.packageName)?.let {
                holder.ivQuestionImage?.let { it1 ->
                    picasso.loadImage(it1, it)
                    it1.visibility = View.VISIBLE
                }
            }
        }
    }

    fun showCurrentChapter(chapter: Int) {
        fabVisibilityController.fabState(true)
        fillFilteredQuestions(questions)
        sectionQuestions.addAll(fillFilteredQuestions(sections[chapter].tests))
        notifyAllSectionsDataSetChanged()
    }

    fun showAllChapters() {
        fabVisibilityController.fabState(false)
        sectionQuestions.clear()
        filteredQuestions = fillFilteredQuestions(questions)
        notifyAllSectionsDataSetChanged()
    }


//    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
//        holder?.recycler?.layoutManager = LinearLayoutManager(holder?.itemView?.context)
//        holder?.recycler?.adapter = AnswersAdapter(object : AnswersAdapter.IAnswers {
//            override fun autoRemoveFromFavourite() {}
//
//            override fun autoAddToFavourite() {}
//
//            override fun addChapterStatistics(index: Int, code: String, chapter: Int, clickedAnswer: Int) {}
//
//            override fun refreshTabLayout() {}
//
//            override fun onAnswerClicked() {}
//
//            override fun addTestProgress(isRight: Boolean, weight: Int, chapter: Int) {}
//        }, filteredQuestions[position], intentId)
//
//
//
//        holder?.tvQuestion?.text = filteredQuestions[position].task ?: ""
//        holder?.tvQuestionCode?.text = filteredQuestions[position].code ?: ""
//        holder?.ivQuestionImage?.visibility = View.GONE
//
//        filteredQuestions[position].image?.let {
//            context.resources?.getIdentifier(it, "drawable", context.packageName)?.let {
//                holder?.ivQuestionImage?.let { it1 ->
//                    picasso.loadImage(it1, it)
//                    it1.visibility = View.VISIBLE
//                }
//            }
//        }
//    }

//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
//            ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_search, parent, false))

    override fun getFilter(): Filter = itemFilter

    class ItemViewHolder(itemView: View?) : SectioningAdapter.ItemViewHolder(itemView) {
        val recycler = itemView?.searchAnswersRecycler
        val tvQuestion = itemView?.tvQuestion
        val tvQuestionCode = itemView?.tvQuestionCode
        val ivQuestionImage = itemView?.ivQuestionImg
    }

    class HeaderViewHolder(itemView: View?) : SectioningAdapter.HeaderViewHolder(itemView) {
        val tvHeader = itemView?.tvHeader
        val headerLayout = itemView?.headerLayout
    }

    private inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {

            val filterString = constraint.toString().toLowerCase()
            val results = Filter.FilterResults()
            val list = if (sectionQuestions.isEmpty()) questions else sectionQuestions
//            val list = questions
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
            filteredQuestions = fillFilteredQuestions(results.values as List<TestingDto>)
            notifyAllSectionsDataSetChanged()
        }

    }

    interface FabVisibilityController {
        fun fabState(showFab: Boolean)
    }
}