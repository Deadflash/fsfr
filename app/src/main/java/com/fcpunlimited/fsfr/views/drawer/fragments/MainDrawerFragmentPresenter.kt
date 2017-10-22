package com.fcpunlimited.fsfr.views.drawer.fragments

import com.fcpunlimited.fsfr.di.contracts.IView
import com.fcpunlimited.fsfr.di.contracts.MainDrawerFragmentContract

/**
 * Created by krotk on 21.10.2017.
 */
class MainDrawerFragmentPresenter : MainDrawerFragmentContract.Presenter {

    var view : MainDrawerFragmentContract.View? = null
    var updateStatisticks : Boolean = true

    override fun bind(iView: IView) {
       view = iView as MainDrawerFragmentContract.View
    }

    override fun getStatistics() {
        if (updateStatisticks) {
            updateStatisticks = false
            view?.showStatistics() ?: println("Error")
        }
    }

    override fun onLayoutClick(examId: Int, examTestType: Int, purchased: Boolean) {
        println("click")
        updateStatisticks = true
    }

    override fun unBind() {
       view = null
    }
}