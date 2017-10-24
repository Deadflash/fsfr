package com.fintrainer.fintrainer.di.contracts

import com.fintrainer.fintrainer.structure.ExamStatisticAndInfo

/**
 * Created by krotk on 21.10.2017.
 */
interface DrawerContract {
    interface View : IView {
        fun showDialog(dialogType: Int)
        fun showStatistics(statistics: ExamStatisticAndInfo)
    }

    interface Presenter : IPresenter {
        fun onLayoutClick(examId: Int, examTestType: Int, purchased: Boolean)
        fun getStatistics(examId: Int)
    }
}