package com.fintrainer.fintrainer.views.testing

import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.di.contracts.TestingContract
import com.fintrainer.fintrainer.structure.DiscussionCommentDto
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.structure.TestingResultsDto
import com.fintrainer.fintrainer.structure.chapters.ChapterStatistics
import com.fintrainer.fintrainer.utils.Constants
import com.fintrainer.fintrainer.utils.Constants.CHAPTER_INTENT
import com.fintrainer.fintrainer.utils.Constants.EXAM_INTENT
import com.fintrainer.fintrainer.utils.Constants.FAILED_TESTS_INTENT
import com.fintrainer.fintrainer.utils.Constants.FAVOURITE_INTENT
import com.fintrainer.fintrainer.utils.Constants.TESTING_INTENT
import com.fintrainer.fintrainer.utils.containers.DiscussionsSyncRealmContainer
import com.fintrainer.fintrainer.utils.containers.GoogleAuthContainer
import com.fintrainer.fintrainer.utils.containers.RealmContainer
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by krotk on 24.10.2017.
 */
class TestingPresenter(private val realmContainer: RealmContainer,
                       private val discussionRealmContainer: DiscussionsSyncRealmContainer,
                       private val authContainer: GoogleAuthContainer) : TestingContract.Presenter,
        DiscussionsSyncRealmContainer.DiscussionRealmCallBack {

    private var view: TestingContract.View? = null
    private var tests: List<TestingDto> = emptyList()
    private var hints: List<DiscussionCommentDto> = emptyList()
    private var failedTests = mutableListOf<TestingDto>()
    private val results: TestingResultsDto = TestingResultsDto(0, 0, 0, 0, 0)
    private val worstChapters = mutableMapOf<Int, Int>()
    //    private var isShown: Boolean = false
    private var account: GoogleSignInAccount? = null
    private var examId: Int? = null
    private var intentId: Int? = null
    private var chapter: Int? = null
    private var chapterStatistics: List<ChapterStatistics>? = null
    private var purchased: Boolean = false

    override fun bind(iView: IView) {
        view = iView as TestingContract.View
//        authContainer.bindTestingActivityView(iView as TestingActivity)
    }

    override fun loadTests(examId: Int, intentId: Int, chapter: Int, purchased: Boolean) {
        this.examId = examId
        this.intentId = intentId
        this.chapter = chapter
        this.purchased = purchased
        if (tests.isEmpty()) {
            if (intentId != EXAM_INTENT) {
                if (checkIsAuthenticatedUser()) {
                    authContainer.getAccount(object : GoogleAuthContainer.AccountCallback {
                        override fun onError(code: Int) {
                            println("need authorize")
                        }

                        override fun onSuccess(account: GoogleSignInAccount?) {
                            account?.let {
                                this@TestingPresenter.account = it
                                discussionRealmContainer.initDiscussionsRealm(it, this@TestingPresenter)
                            }
                        }
                    })
                } else {
                    showTestsWithoutAuth()
                    view?.showNeedAuth()
                }
            } else {
                getTests(intentId, examId, chapter, purchased)
            }
        } else {
            if (intentId == FAILED_TESTS_INTENT) {
                view?.showTest(failedTests)
            } else {
                view?.showTest(tests)
            }
        }
    }

    override fun addChapterStatistics(index: Int, code: String, chapter: Int, clickedAnswer: Int) {
        realmContainer.addChapterStatisticsForChapter(code, index, clickedAnswer, chapter)
    }

    override fun deleteChapterStatistics(index: Int, chapter: Int) {
        realmContainer.deleteChapterStatistics(index, chapter)
    }

    override fun getChapterStatistics(index: Int, chapter: Int): List<ChapterStatistics>? {
        return realmContainer.getChapterStatisticsForExam(index, chapter)
    }

    override fun checkIsAuthenticatedUser(): Boolean = authContainer.isAuthenticated()

    override fun showTestsWithoutAuth() {
        getTests(intentId, examId, chapter, purchased)
    }

    private fun getTests(intentId: Int?, examId: Int?, chapter: Int?, purchased: Boolean) {
        doAsync {
            when (intentId) {
                EXAM_INTENT -> tests = realmContainer.getExamAsync(examId!!)
                TESTING_INTENT ->
                    tests = realmContainer.getTestsAsync(examId!!, purchased)
                CHAPTER_INTENT ->
                    tests = realmContainer.getByChapterAsync(chapter!!, examId!!, purchased)
                FAVOURITE_INTENT ->
                    tests = realmContainer.getFavouriteQuestionsAsync(examId!!)
            }
            uiThread {
                if (intentId != EXAM_INTENT) {
                    if (intentId == CHAPTER_INTENT) {
                        doAsync {
                            chapterStatistics = realmContainer.getChapterStatisticsForExam(examId!!, chapter!!)
                            chapterStatistics.let {
                                for (i in 0 until tests.size) {
                                    it?.forEach(action = { chapterStatistics ->
                                        if (tests[i].code.equals(chapterStatistics.code)) {
                                            tests[i].clicked = true
                                            chapterStatistics.clickedAnswer?.let {
                                                tests[i].rightAnswer = tests[i].answers?.get(it)?.status!!
                                                tests[i].answers?.get(it)?.clicked = true
                                                updateTestStatistics(tests[i].answers?.get(it)?.status!!, tests[i].weight!!, tests[i].chapter!!, i)
                                            }
                                        }
                                    })
                                }
                            }
                            uiThread {
                                if (account == null) {
                                    view?.showTest(tests)
                                } else {
                                    loadHints()
                                }
                            }
                        }
                    } else {
                        if (account == null) {
                            view?.showTest(tests)
                        } else {
                            loadHints()
                        }
                    }
                } else {
                    view?.showTest(tests)
                }
            }
        }
    }

    private fun loadHints() {
        discussionRealmContainer.getApprovedHints(tests, object : DiscussionsSyncRealmContainer.HintsCallback {
            override fun showHints(hints: List<DiscussionCommentDto>) {
                this@TestingPresenter.hints = hints
                view?.showTest(tests)
            }

        })
    }

    override fun getHints(): List<DiscussionCommentDto> = hints

    override fun realmConfigCallback(code: Int) {
        if (code == Constants.REALM_SUCCESS_CONNECT_CODE) {
            getTests(intentId, examId, chapter, purchased)
        } else {
            println("error")
        }
    }

    override fun getLoadedTests(): List<TestingDto> = tests

    override fun updateTestStatistics(isRight: Boolean, weight: Int, chapter: Int, testPosition: Int) {
        if (isRight) {
            results.right = results.right + 1
            results.weight = results.weight + weight
        } else {
            failedTests.add(tests[testPosition])
            results.wrong = results.wrong + 1
            worstChapters.put(chapter, if (worstChapters[chapter] == null) 1 else worstChapters[chapter]?.plus(1)
                    ?: 0)
        }
    }

    override fun checkIsQuestionFavourite(index: Int, type: Int, chapter: Int, isAddRemoveAction: Boolean) {
        doAsync {
            val isFavourite = realmContainer.checkQuestionIsFavouriteAsync(index, type)
            if (!isAddRemoveAction) {
                uiThread {
                    view?.showIsFavouriteQuestion(isFavourite)
                }
            } else {
                realmContainer.addRemoveFavourite(!isFavourite, index, type, chapter)
                uiThread {
                    view?.showIsFavouriteQuestion(!isFavourite)
                }
            }
        }
    }

    override fun addToFavourite() {

    }

    override fun removeFromFavourite(type: Int, index: Int) {
        doAsync {
            val isFavourite = realmContainer.checkQuestionIsFavouriteAsync(index, type)
            if (isFavourite) {
                realmContainer.autoRemoveFromFavourite(type, index)
                uiThread {
                    view?.showIsFavouriteQuestion(false)
                }
            }
        }
    }

    override fun getFailedTests(): List<TestingDto> = failedTests

    override fun showResults() {
        var max = 0
        for (chapter in worstChapters.entries) {
            if (max < chapter.value) {
                max = chapter.value
                results.worthChapter = chapter.key
            }
        }
//        if (!isShown) {
//            isShown = true
        view?.showResults(results)
//        }
    }

    override fun saveTestsResult(intentId: Int, weight: Int, testType: Int, rightAnswers: Int) {
        doAsync {
            realmContainer.saveResults(intentId, weight, testType, rightAnswers)
        }
    }

    override fun unBind() {
        view = null
//        authContainer.unbindTestingActivityView()
    }
}