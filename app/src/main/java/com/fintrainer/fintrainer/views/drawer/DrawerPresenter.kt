package com.fintrainer.fintrainer.views.drawer

import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.di.contracts.DrawerContract

/**
 * Created by krotk on 21.10.2017.
 */
class DrawerPresenter : DrawerContract.Presenter {

    var view : DrawerContract.View? = null
    var updateStatistics: Boolean = true

    override fun bind(iView: IView) {
       view = iView as DrawerContract.View
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