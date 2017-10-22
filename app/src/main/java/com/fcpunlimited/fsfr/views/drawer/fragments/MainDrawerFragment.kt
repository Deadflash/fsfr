package com.fcpunlimited.fsfr.views.drawer.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.fcpunlimited.fsfr.R
import com.fcpunlimited.fsfr.di.contracts.MainDrawerFragmentContract
import com.fcpunlimited.fsfr.utils.Constants.DRAWER_MAIN_FRAGMENT_TAG
import com.fcpunlimited.fsfr.views.App
import com.fcpunlimited.fsfr.views.BaseFragment
import com.fcpunlimited.fsfr.views.testing.TestingActivity
import org.jetbrains.anko.startActivityForResult
import icepick.State
import kotlinx.android.synthetic.main.fragment_drawer_main.*
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject


/**
 * Created by krotk on 21.10.2017.
 */
class MainDrawerFragment : BaseFragment(), MainDrawerFragmentContract.View, View.OnClickListener {

    @State @JvmField
    var examProgress : Int? = null

    @State @JvmField
    var trainingProgress : Int? = null

    @Inject
    lateinit var presenter: MainDrawerFragmentPresenter

    private val animatorSet = AnimatorSet()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.mainComponent.inject(this)
        presenter.bind(this)

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        setupUserProgress()
        presenter.getStatistics()
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
        if (!animatorSet.isRunning) {
            //receive from db
            examProgress = 100
            trainingProgress = 20
            val exam = ObjectAnimator.ofInt(examProgressBar,"progress",0,100)
            exam.addUpdateListener {
                tvExamProgressCount?.text = "Средний бал: ${exam.animatedValue}"
            }
            val train = ObjectAnimator.ofInt(trainingProgressBar,"progress",0,20)
            train.addUpdateListener {
                tvTrainingProgressCount?.text = "В среднем ${train.animatedValue} балов"
            }
            animatorSet.playTogether(exam,train)
            animatorSet.duration = 2000
            animatorSet.startDelay = 1000
            animatorSet.interpolator = DecelerateInterpolator()
            animatorSet.start()
        }
    }

    override fun onPause() {
        super.onPause()
        animatorSet.cancel()
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
        chapters_layout.id -> presenter.onLayoutClick(0, 2, false)
        search_layout.id -> presenter.onLayoutClick(0, 3, false)
        favourite_layout.id -> presenter.onLayoutClick(0, 4, false)
        else -> println("miss click")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        toast("Result Code $resultCode Request Code $requestCode")
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showDialog(dialogType: Int) {

    }

    override fun getFragmentLayout(): Int = R.layout.fragment_drawer_main

    override fun getFragmentTag(): String = DRAWER_MAIN_FRAGMENT_TAG


}