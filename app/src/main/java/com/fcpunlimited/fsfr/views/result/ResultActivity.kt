package com.fcpunlimited.fsfr.views.result

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import com.fcpunlimited.fsfr.R
import com.fcpunlimited.fsfr.views.BaseActivity
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.sdk25.coroutines.onClick


class ResultActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ivBuyCart.setColorFilter(ContextCompat.getColor(this, R.color.blue_grey_500))
        setupAnimations()

        purchase_layout.onClick {

        }

        btShowFailedQuestions.onClick {

        }
        btToMenu.onClick {
            resultCardView.visibility = View.GONE
            purchaseCardView.visibility = View.GONE
            tvTitle.visibility = View.GONE
            tvAdvise.visibility = View.GONE
            tvTitle.alpha = 0F
            tvAdvise.alpha = 0F
            setupAnimations()
        }
    }

    private fun setupAnimations() {
        val cardResultCardViewAnimator = ObjectAnimator.ofFloat(resultCardView, "translationX", -500F, 0F)
        cardResultCardViewAnimator.duration = 700
        cardResultCardViewAnimator.startDelay = 500
        cardResultCardViewAnimator.interpolator = DecelerateInterpolator()
        cardResultCardViewAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {

                val resultProgressBarAnimator = ObjectAnimator.ofInt(resultProgressbar, "progress", 0, 100)
                resultProgressBarAnimator.duration = 1500
                resultProgressBarAnimator.interpolator = DecelerateInterpolator()
                resultProgressBarAnimator.addUpdateListener { tvResult.text = resultProgressBarAnimator.animatedValue.toString() }
                resultProgressBarAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}

                    override fun onAnimationEnd(p0: Animator?) {
                        tvTitle.visibility = View.VISIBLE
                        tvAdvise.visibility = View.VISIBLE
                        val titleHeightAnimator = ValueAnimator.ofInt(0, 70)
                        titleHeightAnimator.duration = 500
                        titleHeightAnimator.addUpdateListener {
                            val titleLayoutParams = tvTitle.layoutParams
                            val adviseLayoutParams = tvAdvise.layoutParams
                            titleLayoutParams.height = titleHeightAnimator.animatedValue as Int
                            adviseLayoutParams.height = titleHeightAnimator.animatedValue as Int
                            tvTitle.layoutParams = titleLayoutParams
                            tvAdvise.layoutParams = adviseLayoutParams
                        }
                        val greetingAlphaAnimator = ValueAnimator.ofFloat(0F, 1F)
                        greetingAlphaAnimator.duration = 700
                        greetingAlphaAnimator.startDelay = 500
                        greetingAlphaAnimator.addUpdateListener {
                            val currentAlpha = greetingAlphaAnimator.animatedValue as Float
                            tvTitle.alpha = currentAlpha
                            tvAdvise.alpha = currentAlpha
                        }
                        val titleAnimatorSet = AnimatorSet()
                        titleAnimatorSet.playTogether(titleHeightAnimator, greetingAlphaAnimator)
                        titleAnimatorSet.start()

                        titleAnimatorSet.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(p0: Animator?) {}

                            override fun onAnimationEnd(p0: Animator?) {
                                purchaseCardView.visibility = View.VISIBLE
                                val purchaseAnimator: ObjectAnimator = ObjectAnimator.ofFloat(purchaseCardView, "translationX", 300F, 0F)
                                purchaseAnimator.duration = 900
                                purchaseAnimator.interpolator = BounceInterpolator()
                                purchaseAnimator.start()
                            }

                            override fun onAnimationCancel(p0: Animator?) {}

                            override fun onAnimationStart(p0: Animator?) {}
                        })
                    }

                    override fun onAnimationStart(p0: Animator?) {}

                    override fun onAnimationCancel(p0: Animator?) {}
                })

                resultProgressBarAnimator.start()

            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {
                resultCardView.visibility = View.VISIBLE
            }
        })
        cardResultCardViewAnimator.start()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }
}
