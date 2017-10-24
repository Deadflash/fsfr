package com.fintrainer.fintrainer.views.testing

import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.di.contracts.TestingContract
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.utils.realm.RealmContainer
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by krotk on 24.10.2017.
 */
class TestingPresenter(private val realmContainer: RealmContainer) : TestingContract.Presenter {

    private var view: TestingContract.View? = null
    private var tests: List<TestingDto> = emptyList()

    override fun bind(iView: IView) {
        view = iView as TestingContract.View
    }

    override fun loadTests(examId: Int, intentId: Int) {
        doAsync {
            tests = realmContainer.getTestsAsync(intentId, examId)
            uiThread {
                if (view != null){
                    view?.showTest(tests)
                }
            }
        }
    }

    override fun getLoadedTests(): List<TestingDto> = tests

    override fun unBind() {
        view = null
    }
}