package com.fintrainer.fintrainer.views.drawer

import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.di.contracts.DrawerContract
import com.fintrainer.fintrainer.utils.realm.RealmContainer

/**
 * Created by krotk on 21.10.2017.
 */
class DrawerPresenter(private var realmContainer: RealmContainer) : DrawerContract.Presenter {

    var view: DrawerContract.View? = null
    var updateStatistics: Boolean = true

    override fun bind(iView: IView) {
        view = iView as DrawerContract.View
    }

    override fun getStatistics(examId: Int) {
//        if (updateStatistics) {
//            updateStatistics = false
            val examInfo = realmContainer.getExamInformation(examId)
            view?.showStatistics(examInfo) ?: println("Error")
//        }
    }

    override fun onLayoutClick(examId: Int, examTestType: Int, purchased: Boolean) {
        updateStatistics = true
    }

    override fun unBind() {
        view = null
    }
}