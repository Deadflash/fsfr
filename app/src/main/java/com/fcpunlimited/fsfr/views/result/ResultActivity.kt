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

    private val progressLayoutAnim = ValueAnimator.ofFloat(0F, 1F)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ivBuyCart.setColorFilter(ContextCompat.getColor(this, R.color.blue_grey_500))
        setupAnimations()
        startAnimation()
        purchase_layout.onClick {

        }

        btShowFailedQuestions.onClick {

        }
        btToMenu.onClick {
            //            resultCardView.visibility = View.GONE
            progressbar_layout.alpha = 0F
            purchaseCardView.visibility = View.GONE
            tvTitle.alpha = 0F
            tvAdvise.alpha = 0F
            startAnimation()
        }
    }

    private fun setupAnimations() {
        progressLayoutAnim.duration = 700
        progressLayoutAnim.startDelay = 500
        progressLayoutAnim.addUpdateListener { progressbar_layout.alpha = progressLayoutAnim.animatedValue as Float }
        progressLayoutAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {

                val resultProgressBarAnimator = ObjectAnimator.ofInt(resultProgressbar, "progress", 0, 100)
                resultProgressBarAnimator.duration = 1200
                resultProgressBarAnimator.interpolator = DecelerateInterpolator()
                resultProgressBarAnimator.addUpdateListener { tvResult.text = resultProgressBarAnimator.animatedValue.toString() }
                resultProgressBarAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}

                    override fun onAnimationEnd(p0: Animator?) {

                        val greetingAlphaAnimator = ValueAnimator.ofFloat(0F, 1F)
                        greetingAlphaAnimator.duration = 500
                        greetingAlphaAnimator.startDelay = 100
                        greetingAlphaAnimator.addUpdateListener {
                            val currentAlpha = greetingAlphaAnimator.animatedValue as Float
                            tvTitle.alpha = currentAlpha
                            tvAdvise.alpha = currentAlpha
                        }

                        val purchaseAnimator: ObjectAnimator = ObjectAnimator.ofFloat(purchaseCardView, "translationX", 300F, 0F)
                        purchaseAnimator.addUpdateListener { purchaseCardView.visibility = View.VISIBLE }
                        purchaseAnimator.duration = 900
                        purchaseAnimator.startDelay = 800
                        purchaseAnimator.interpolator = BounceInterpolator()

                        val animSet = AnimatorSet()
                        animSet.playTogether(greetingAlphaAnimator, purchaseAnimator)
                        animSet.start()
                    }

                    override fun onAnimationStart(p0: Animator?) {}

                    override fun onAnimationCancel(p0: Animator?) {}
                })
                resultProgressBarAnimator.start()
            }

        })
    }

    private fun startAnimation() {
        progressLayoutAnim.start()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }
}
