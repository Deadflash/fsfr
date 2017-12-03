package com.fintrainer.fintrainer.di.contracts

import com.fintrainer.fintrainer.structure.DiscussionCommentDto
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.structure.TestingResultsDto

/**
 * Created by krotk on 24.10.2017.
 */
interface TestingContract {
    interface View : IView {
        fun showTest(tests: List<TestingDto>)
        fun showHints(hints: List<DiscussionCommentDto>)
        fun showResults(testingResultsDto: TestingResultsDto)
        fun showIsFavouriteQuestion(isFavourite: Boolean)
        fun showNeedAuth()
    }

    interface Presenter : IPresenter {
        fun loadTests(examId: Int, intentId: Int, chapter: Int)
        fun showTestsWithoutAuth()
        fun getLoadedTests(): List<TestingDto>
        fun getHints(): List<DiscussionCommentDto>
        fun updateTestStatistics(isRight: Boolean, weight: Int, chapter: Int, testPosition: Int)
        fun showResults()
        fun getFailedTests(): List<TestingDto>
        fun saveTestsResult(intentId: Int, weight: Int, testType: Int, rightAnswers: Int)
        fun checkIsQuestionFavourite(index: Int, type: Int,chapter: Int, isAddRemoveAction: Boolean)
        fun addToFavourite()
        fun removeFromFavourite()
    }
}