package com.fintrainer.fintrainer.views.search

import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.di.contracts.SearchContract
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.utils.RealmContainer
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by krotk on 25.10.2017.
 */

class SearchPresenter(private val realmContainer: RealmContainer): SearchContract.Presenter {

    private var view: SearchContract.View? = null
    private var  questions: List<TestingDto> = emptyList()

    override fun bind(iView: IView) {
       view = iView as SearchActivity
    }

    override fun loadQuestions(examId: Int) {
        if (questions.isEmpty()){
            doAsync {
                questions = realmContainer.getAllQuestionsAsync(examId)
                uiThread {
                    view?.showQuestions(questions)
                }
            }
        }else{
            view?.showQuestions(questions)
        }
    }

    override fun unBind() {
        view = null
    }
}