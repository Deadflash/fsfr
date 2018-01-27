package com.fintrainer.fintrainer.di.contracts

import com.fintrainer.fintrainer.structure.ChapterRealm

/**
 * Created by krotk on 25.10.2017.
 */
interface ChaptersContract {
    interface View : IView {
        fun showChapters(chapters: List<ChapterRealm>)
    }

    interface Presenter : IPresenter {
        fun checkChapterStatisticsForExist(index: Int, chapter: Int): Boolean
        fun deleteChapterStatistics(index: Int, chapter: Int)
        fun loadChapters(examId: Int)
    }
}