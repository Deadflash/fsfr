package com.fintrainer.fintrainer.views.testing

import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.di.contracts.TestingContract
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.structure.TestingResultsDto
import com.fintrainer.fintrainer.utils.realm.RealmContainer
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by krotk on 24.10.2017.
 */
class TestingPresenter(private val realmContainer: RealmContainer) : TestingContract.Presenter {

    private var view: TestingContract.View? = null
    private var tests: List<TestingDto> = emptyList()
    private val results: TestingResultsDto = TestingResultsDto(0, 0, 0, 0, 0)
    private val worstChapters = mutableMapOf<Int, Int>()
    private var isShown: Boolean = false

    override fun bind(iView: IView) {
        view = iView as TestingContract.View
    }

    override fun loadTests(examId: Int, intentId: Int) {
        if (tests.isEmpty()) {
            doAsync {
                tests = realmContainer.getTestsAsync(intentId, examId)
                uiThread {
                    if (view != null) {
                        view?.showTest(tests)
                    }
                }
            }
        } else {
            if (view != null) {
                view?.showTest(tests)
            }
        }
    }

    override fun getLoadedTests(): List<TestingDto> = tests

    override fun updateTestStatistics(isRight: Boolean, weight: Int, chapter: Int) {
        if (isRight) {
            results.right = results.right + 1
            results.weight = results.weight + weight
        } else {
            results.wrong = results.wrong + 1
            worstChapters.put(chapter, if (worstChapters[chapter] == null) 1 else worstChapters[chapter]?.plus(1) ?: 0)
        }
    }

    override fun showResults() {
        var max = 0
        for (chapter in worstChapters.entries) {
            if (max < chapter.value) {
                max = chapter.value
                results.worthChapter = chapter.key
            }
        }
        if (!isShown){
            isShown = true
            view?.showResults(results)
        }
    }

    override fun unBind() {
        view = null
    }
}