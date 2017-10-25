package com.fintrainer.fintrainer.di.contracts

import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.structure.TestingResultsDto

/**
 * Created by krotk on 24.10.2017.
 */
interface TestingContract {
    interface View : IView {
        fun showTest(tests: List<TestingDto>)
        fun showResults(testingResultsDto: TestingResultsDto)
    }

    interface Presenter : IPresenter {
        fun loadTests(examId: Int, intentId: Int)
        fun getLoadedTests(): List<TestingDto>
        fun updateTestStatistics(isRight: Boolean, weight: Int, chapter: Int, testPosition: Int)
        fun showResults()
        fun getFailedTests(): List<TestingDto>
    }
}