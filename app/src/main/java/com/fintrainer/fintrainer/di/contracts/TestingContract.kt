package com.fintrainer.fintrainer.di.contracts

import com.fintrainer.fintrainer.structure.TestingDto

/**
 * Created by krotk on 24.10.2017.
 */
interface TestingContract {
    interface View : IView {
        fun showTest(tests: List<TestingDto>)
    }

    interface Presenter : IPresenter {
        fun loadTests(examId: Int, intentId: Int)
        fun getLoadedTests(): List<TestingDto>
    }
}