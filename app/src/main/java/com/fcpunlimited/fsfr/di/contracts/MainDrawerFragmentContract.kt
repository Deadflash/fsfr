package com.fcpunlimited.fsfr.di.contracts

import com.fcpunlimited.fsfr.structure.PurchasesStruct

/**
 * Created by krotk on 21.10.2017.
 */
interface MainDrawerFragmentContract {
    interface View : IView {
        fun showDialog(dialogType: Int)
        fun showStatistics()
    }

    interface Presenter : IPresenter {
        fun onLayoutClick(examId: Int, examTestType: Int, purchased: Boolean)
        fun getStatistics()
    }
}