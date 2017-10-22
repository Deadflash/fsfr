package com.fcpunlimited.fsfr.views.drawer.fragments

import com.fcpunlimited.fsfr.di.contracts.IView
import com.fcpunlimited.fsfr.di.contracts.MainDrawerFragmentContract

/**
 * Created by krotk on 21.10.2017.
 */
class MainDrawerFragmentPresenter : MainDrawerFragmentContract.Presenter {

    var view : MainDrawerFragmentContract.View? = null
    var updateStatistics: Boolean = true

    override fun bind(iView: IView) {
       view = iView as MainDrawerFragmentContract.View
    }

    override fun getStatistics() {
        if (updateStatistics) {
            updateStatistics = false
            view?.showStatistics() ?: println("Error")
        }
    }

    override fun onLayoutClick(examId: Int, examTestType: Int, purchased: Boolean) {
        println("click")
        updateStatistics = true
    }

    override fun unBind() {
       view = null
    }
}