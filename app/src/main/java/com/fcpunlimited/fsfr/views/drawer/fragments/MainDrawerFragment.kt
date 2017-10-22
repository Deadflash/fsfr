package com.fcpunlimited.fsfr.views.drawer.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.fcpunlimited.fsfr.R
import com.fcpunlimited.fsfr.di.contracts.MainDrawerFragmentContract
import com.fcpunlimited.fsfr.utils.Constants.DRAWER_MAIN_FRAGMENT_TAG
import com.fcpunlimited.fsfr.views.BaseFragment
import com.fcpunlimited.fsfr.views.chapters.ChaptersActivity
import com.fcpunlimited.fsfr.views.testing.TestingActivity
import icepick.State
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject


/**
 * Created by krotk on 21.10.2017.
 */
class MainDrawerFragment : BaseFragment(), MainDrawerFragmentContract.View, View.OnClickListener {

    @State
    @JvmField
    var examProgress: Int? = null

    @State
    @JvmField
    var trainingProgress: Int? = null

    @Inject
    lateinit var presenter: MainDrawerFragmentPresenter

    private val statisticsAnimSet = AnimatorSet()
    private val cardViewAnimSet = AnimatorSet()
    private val cardViewReverseAnimSet = AnimatorSet()
    private val duration: Long = 400
    private val delayDuration: Long = 100

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        App.mainComponent.inject(this)
        presenter.bind(this)

        setupClickListeners()
        setupAnimations()
        setupReverseAnimation()
    }

    override fun onResume() {
        super.onResume()
        setupUserProgress()
        presenter.getStatistics()
    }

    private fun setupAnimations() {
        exam_card_view.visibility = View.INVISIBLE
        training_card_view.visibility = View.INVISIBLE
        chapters_card_view.visibility = View.INVISIBLE
        search_card_view.visibility = View.INVISIBLE
        favourite_card_view.visibility = View.INVISIBLE

        val exAnim = ObjectAnimator.ofFloat(exam_card_view, "translationX", -500F, 0F)
        exAnim.duration = duration
        exAnim.startDelay = delayDuration
        exAnim.interpolator = DecelerateInterpolator()
        exAnim.addUpdateListener { exam_card_view.visibility = View.VISIBLE }

        val trainAnim = ObjectAnimator.ofFloat(training_card_view, "translationX", -500F, 0F)
        trainAnim.duration = duration
        trainAnim.startDelay = delayDuration + 100
        trainAnim.interpolator = DecelerateInterpolator()
        trainAnim.addUpdateListener { training_card_view.visibility = View.VISIBLE }

        val chaptersAnim = ObjectAnimator.ofFloat(chapters_card_view, "translationX", -500F, 0F)
        chaptersAnim.duration = duration
        chaptersAnim.startDelay = delayDuration + 200
        chaptersAnim.interpolator = DecelerateInterpolator()
        chaptersAnim.addUpdateListener { chapters_card_view.visibility = View.VISIBLE }

        val searchAnim = ObjectAnimator.ofFloat(search_card_view, "translationX", -500F, 0F)
        searchAnim.duration = duration
        searchAnim.startDelay = delayDuration + 300
        searchAnim.interpolator = DecelerateInterpolator()
        searchAnim.addUpdateListener { search_card_view.visibility = View.VISIBLE }

        val favAnim = ObjectAnimator.ofFloat(favourite_card_view, "translationX", -500F, 0F)
        favAnim.duration = duration
        favAnim.startDelay = delayDuration + 400
        favAnim.interpolator = DecelerateInterpolator()
        favAnim.addUpdateListener { favourite_card_view.visibility = View.VISIBLE }

        cardViewAnimSet.playTogether(exAnim, trainAnim, chaptersAnim, searchAnim, favAnim)
        cardViewAnimSet.startDelay = 200
        startAnimations()
    }

    private fun setupReverseAnimation() {
        val exAnim = ObjectAnimator.ofFloat(exam_card_view, "translationX", 0F, -500F)
        exAnim.duration = duration
        exAnim.startDelay = delayDuration
        exAnim.interpolator = DecelerateInterpolator()
        exAnim.addUpdateListener { exam_card_view.visibility = View.VISIBLE }

        val trainAnim = ObjectAnimator.ofFloat(training_card_view, "translationX", 0F, -500F)
        trainAnim.duration = duration
        trainAnim.startDelay = delayDuration + 100
        trainAnim.interpolator = DecelerateInterpolator()
        trainAnim.addUpdateListener { training_card_view.visibility = View.VISIBLE }

        val chaptersAnim = ObjectAnimator.ofFloat(chapters_card_view, "translationX", 0F, -500F)
        chaptersAnim.duration = duration
        chaptersAnim.startDelay = delayDuration + 200
        chaptersAnim.interpolator = DecelerateInterpolator()
        chaptersAnim.addUpdateListener { chapters_card_view.visibility = View.VISIBLE }

        val searchAnim = ObjectAnimator.ofFloat(search_card_view, "translationX", 0F, -500F)
        searchAnim.duration = duration
        searchAnim.startDelay = delayDuration + 300
        searchAnim.interpolator = DecelerateInterpolator()
        searchAnim.addUpdateListener { search_card_view.visibility = View.VISIBLE }

        val favAnim = ObjectAnimator.ofFloat(favourite_card_view, "translationX", 0F, -500F)
        favAnim.duration = duration
        favAnim.startDelay = delayDuration + 400
        favAnim.interpolator = DecelerateInterpolator()
        favAnim.addUpdateListener { favourite_card_view.visibility = View.VISIBLE }

        cardViewReverseAnimSet.playTogether(exAnim, trainAnim, chaptersAnim, searchAnim, favAnim)
        cardViewReverseAnimSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
                startAnimations()
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {}

        })
    }

    private fun startAnimations() {
        cardViewAnimSet.start()
    }

    private fun startReverseAnimations() {
        cardViewReverseAnimSet.start()
    }

    private fun setupUserProgress() {
        tvExamProgressCount.text = "Средний бал: ${examProgress?.toString() ?: "0"}"
        examProgressBar.progress = examProgress ?: 0
        tvTrainingProgressCount.text = "В среднем ${trainingProgress?.toString() ?: "0"} балов"
        trainingProgressBar.progress = trainingProgress ?: 0
    }

    private fun setupClickListeners() {
        exam_Layout.setOnClickListener(this)
        training_layout.setOnClickListener(this)
        chapters_layout.setOnClickListener(this)
        search_layout.setOnClickListener(this)
        favourite_layout.setOnClickListener(this)
    }

    override fun showStatistics() {
        if (!statisticsAnimSet.isRunning) {
            //receive from db
            examProgress = 100
            trainingProgress = 20
            val exam = ObjectAnimator.ofInt(examProgressBar, "progress", 0, 100)
            exam.addUpdateListener {
                tvExamProgressCount?.text = "Средний бал: ${exam.animatedValue}"
            }
            val train = ObjectAnimator.ofInt(trainingProgressBar, "progress", 0, 20)
            train.addUpdateListener {
                tvTrainingProgressCount?.text = "В среднем ${train.animatedValue} балов"
            }
            statisticsAnimSet.playTogether(exam, train)
            statisticsAnimSet.duration = 2000
            statisticsAnimSet.startDelay = 1000
            statisticsAnimSet.interpolator = DecelerateInterpolator()
            statisticsAnimSet.start()
        }
    }

    override fun onPause() {
        super.onPause()
        statisticsAnimSet.cancel()
        presenter.unBind()
    }

    override fun onClick(p0: View?) = when (p0?.id) {
        exam_Layout.id -> {
            startActivityForResult<TestingActivity>(1)
        }
        training_layout.id -> {
            presenter.onLayoutClick(0, 0, false)
            presenter.getStatistics()
        }
        chapters_layout.id -> {
            presenter.onLayoutClick(0, 2, false)
            startActivity<ChaptersActivity>()
        }
        search_layout.id -> {
            presenter.onLayoutClick(0, 3, false)
            exam_card_view.visibility = View.INVISIBLE
            training_card_view.visibility = View.INVISIBLE
            chapters_card_view.visibility = View.INVISIBLE
            search_card_view.visibility = View.INVISIBLE
            favourite_card_view.visibility = View.INVISIBLE
            startAnimations()
            showStatistics()
        }
        favourite_layout.id -> {
            presenter.onLayoutClick(0, 4, false)
            startReverseAnimations()
        }
        else -> println("miss click")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        toast("Result Code $resultCode Request Code $requestCode")
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showDialog(dialogType: Int) {

    }

    override fun getFragmentLayout(): Int = R.layout.drawer_main

    override fun getFragmentTag(): String = DRAWER_MAIN_FRAGMENT_TAG


}