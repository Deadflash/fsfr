package com.fintrainer.fintrainer.views.result

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.utils.Constants.EXAM_INTENT
import com.fintrainer.fintrainer.utils.Constants.FAILED_TESTS_INTENT
import com.fintrainer.fintrainer.utils.Constants.FAVOURITE_INTENT
import com.fintrainer.fintrainer.utils.Constants.TESTING_INTENT
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseActivity
import com.fintrainer.fintrainer.views.drawer.DrawerActivity
import com.fintrainer.fintrainer.views.testing.TestingActivity
import com.fintrainer.fintrainer.views.testing.TestingPresenter
import icepick.State
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import javax.inject.Inject


class ResultActivity : BaseActivity() {

    @State
    @JvmField
    var isResultsSave: Boolean = false

    @Inject
    lateinit var presenter: TestingPresenter

    private val progressLayoutAnim = ValueAnimator.ofFloat(0F, 1F)
    private var rightAnswers: Int? = null
    private var wrongAnswers: Int? = null
    private var testType: Int? = null
    private var intentId: Int? = null
    private var purchaised: Boolean? = null
    private var isStandalone: Boolean? = null
    private var worthChapter: Int? = null
    private var weight: Int? = null
    private var progressValue: Int? = null
    private var translationX: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        App.initTestingComponent()?.inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.results)
        ivBuyCart.setColorFilter(ContextCompat.getColor(this, R.color.blue_grey_500))

        val displayMetrics = DisplayMetrics()
        (applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        translationX = displayMetrics.widthPixels.toFloat()/2

        setupAnimations()
        startAnimation()

        rightAnswers = intent.getIntExtra("right", 0)
        wrongAnswers = intent.getIntExtra("wrong", 0)
        worthChapter = intent.getIntExtra("worthChapter", 0)
        intentId = intent.getIntExtra("intentId", 0)
        isStandalone = intent.getBooleanExtra("standaloned", false)
        testType = intent.getIntExtra("testType", 0)
        purchaised = intent.getBooleanExtra("purchased", false)
        weight = intent.getIntExtra("weight", 0)

        resultProgressbar.max = rightAnswers!! + wrongAnswers!!

        setViews()

        purchase_layout.onClick {
            startActivity(intentFor<DrawerActivity>("buy" to true).clearTop())
            finish()
        }

        btShowFailedQuestions.onClick {
            finish()
            startActivityForResult<TestingActivity>(FAILED_TESTS_INTENT,"intentId" to FAILED_TESTS_INTENT)
        }
        btToMenu.onClick {
            onBackPressed()
        }
        if (!isResultsSave && intentId == EXAM_INTENT || intentId == TESTING_INTENT){
            isResultsSave = true
            presenter.saveTestsResult(intentId ?: -1,weight ?: -1,testType ?: -1,rightAnswers ?: -1)
        }
    }

    override fun onResume() {
        super.onResume()
        resultProgressbar.progress = 0
    }

    private fun setViews() {
        if (wrongAnswers == 0){
            btShowFailedQuestions.visibility = View.GONE
        }
        when (intentId) {
            EXAM_INTENT -> {
                progressValue = weight
                btShowFailedQuestions.visibility = View.GONE
                if (weight ?: 0 > 80) {
                    tvTitle.text = "Вы сдали экзамен, Поздравляем!!!"
                } else {
                    tvTitle.text = "Экзамен не сдан!"
                }
                showAdvise()
            }
            TESTING_INTENT -> {
                progressValue = rightAnswers
                if (rightAnswers ?: 0 > 80) {
                    tvTitle.text = "Вы вполне можете сдать экзамен!"
                } else {
                    tvTitle.text = "Тренеруйтесь больше!"
                }
                showAdvise()
            }

            FAVOURITE_INTENT -> {
                progressValue = rightAnswers
                tvTitle.text = "Результаты"
            }
            else -> {
                progressValue = rightAnswers
                if (rightAnswers ?: 0 > 80) {
                    tvTitle.text = "Отличный результат!"
                } else {
                    tvTitle.text = "Попробуйте еще раз!"
                }
                tvAdvise.visibility = View.GONE
            }
        }
    }

    private fun showAdvise(){
        if (worthChapter ?: 0 > 0) {
            tvAdvise.text = "Советуем повторить $worthChapter главу"
        }
    }

    private fun setupAnimations() {
        progressLayoutAnim.duration = 400
        progressLayoutAnim.startDelay = 500
        progressLayoutAnim.addUpdateListener { progressbar_layout.alpha = progressLayoutAnim.animatedValue as Float }
        progressLayoutAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {

                val resultProgressBarAnimator = ObjectAnimator.ofInt(resultProgressbar, "progress", 0, progressValue ?: 0)
                resultProgressBarAnimator.duration = 800
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

                        val purchaseAnimator: ObjectAnimator = ObjectAnimator.ofFloat(purchaseCardView, "translationX", translationX, 0F)
                        purchaseAnimator.addUpdateListener { purchaseCardView.visibility = View.VISIBLE }
                        purchaseAnimator.duration = 900
                        purchaseAnimator.startDelay = 600
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.unBind()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }
}
