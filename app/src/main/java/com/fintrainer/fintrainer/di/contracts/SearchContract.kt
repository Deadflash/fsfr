package com.fintrainer.fintrainer.di.contracts

import com.fintrainer.fintrainer.structure.TestingDto

/**
 * Created by krotk on 25.10.2017.
 */
interface SearchContract {

    interface View : IView {
        fun showQuestions(questions: List<TestingDto>)
    }

    interface Presenter : IPresenter {
      fun loadQuestions(examId: Int)
    }
}