package com.fintrainer.fintrainer.views.testing.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ScrollView
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.AnswersAdapter
import com.fintrainer.fintrainer.structure.DiscussionCommentDto
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.utils.Constants.FAILED_TESTS_INTENT
import com.fintrainer.fintrainer.utils.Constants.TESTING_FRAGMENT_TAG
import com.fintrainer.fintrainer.utils.IPageSelector
import com.fintrainer.fintrainer.utils.containers.PicassoContainer
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseFragment
import com.fintrainer.fintrainer.views.testing.TestingActivity
import com.fintrainer.fintrainer.views.testing.TestingPresenter
import kotlinx.android.synthetic.main.fragment_testing.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.onUiThread
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

/**
 * Created by krotk on 22.10.2017.
 */
class TestingFragment : BaseFragment(), AnswersAdapter.IAnswers {

    @Inject
    lateinit var presenter: TestingPresenter
    @Inject
    lateinit var picasso: PicassoContainer

    private lateinit var tests: List<TestingDto>
    private var pageSelector: IPageSelector? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        pageSelector = context as TestingActivity
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.initTestingComponent()?.inject(this)
        tests = if (activity.intent.getIntExtra("intentId", -1) != -1 && activity.intent.getIntExtra("intentId", -1) == FAILED_TESTS_INTENT) {
            presenter.getFailedTests()
        } else {
            presenter.getLoadedTests()
        }
        recycler.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean = false
        }
        val position: Int = arguments.getInt("position", -1)
        if (!tests.isEmpty()) {
            if (position != -1) {
                tvQuestionCode.text = tests[position].code ?: ""
                tvQuestion.text = tests[position].task ?: ""
                if (tests[position].image != null && activity.resources.getIdentifier(tests[position].image, "drawable", activity.packageName) != 0) {
                    ivTestingImg.visibility = View.VISIBLE
                    picasso.loadImage(ivTestingImg, activity.resources.getIdentifier(tests[position].image, "drawable", activity.packageName))
                }

                recycler.adapter = AnswersAdapter(this, tests[position], activity.intent.getIntExtra("intentId", -1))
                recycler.clearFocus()
                recycler.isNestedScrollingEnabled = false
            }
        }

        val comment = if (!presenter.getHints().isEmpty()) presenter.getHints()[position] else null
        if (comment?.text != null) {
            solutionCardView.visibility = View.VISIBLE
            tvSolution.text = comment.text

            showHintLayout.onClick {
                if (solutionLayout.visibility == View.VISIBLE) {
                    solutionLayout.visibility = View.GONE
                    tvShowHint.text = getString(R.string.show_solution)
                    ivHintIndicator.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)

                } else {
                    solutionLayout.visibility = View.VISIBLE
                    tvShowHint.text = getString(R.string.hide_hint)
                    ivHintIndicator.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                }
                scrollView.post({ scrollView.fullScroll(View.FOCUS_DOWN) })
            }
        }else{
            solutionCardView.visibility = View.GONE
        }
    }

    override fun addTestProgress(isRight: Boolean, weight: Int, chapter: Int) {
        presenter.updateTestStatistics(isRight, weight, chapter, arguments.getInt("position", 0))
    }

    override fun onAnswerClicked() {
        val position: Int = arguments.getInt("position", -1)
        if (position != -1) {
            var moveTo: Int
            doAsync {
                moveTo = checkIfClicked(position)
                if (moveTo >= tests.size) {
                    moveTo = 0
                }
                onUiThread {
                    pageSelector?.changePage(moveTo)
                    (activity as? TestingActivity)?.recycler?.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun refreshTabLayout() {
        (activity as? TestingActivity)?.recycler?.adapter?.notifyDataSetChanged()
    }

    private fun checkIfClicked(position: Int): Int {
        if (tests[position].clicked == true) {
            (position until tests.size)
                    .filter { tests[it].clicked == false || tests[it].clicked == null }
                    .forEach { return it }

            (0 until position)
                    .filter { tests[it].clicked == false || tests[it].clicked == null }
                    .forEach { return it }
            onUiThread {
                presenter.showResults()
            }
        }
        return position
    }

    override fun onDetach() {
        super.onDetach()
        pageSelector = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        toast("Result Code $resultCode Request Code $requestCode")
    }

    override fun getFragmentLayout(): Int = R.layout.fragment_testing

    override fun getFragmentTag(): String = TESTING_FRAGMENT_TAG
}