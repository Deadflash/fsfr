package com.fintrainer.fintrainer.di.contracts

/**
 * Created by krotk on 21.10.2017.
 */
interface DrawerContract {
    interface View : IView {
        fun showDialog(dialogType: Int)
        fun showStatistics()
    }

    interface Presenter : IPresenter {
        fun onLayoutClick(examId: Int, examTestType: Int, purchased: Boolean)
        fun getStatistics()
    }
}