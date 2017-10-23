package com.fintrainer.fintrainer.views.drawer

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.di.contracts.DrawerContract
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseActivity
import com.fintrainer.fintrainer.views.chapters.ChaptersActivity
import com.fintrainer.fintrainer.views.drawer.fragments.DrawerPresenter
import com.fintrainer.fintrainer.views.search.SearchActivity
import com.fintrainer.fintrainer.views.testing.TestingActivity
import com.readystatesoftware.systembartint.SystemBarTintManager
import icepick.State
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.drawer_main.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import javax.inject.Inject

class DrawerActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerContract.View, View.OnClickListener {

    @State
    @JvmField
    var examProgress: Int? = null

    @State
    @JvmField
    var trainingProgress: Int? = null

    @Inject
    lateinit var presenter: DrawerPresenter

    private val statisticsAnimSet = AnimatorSet()
    private val cardViewAnimSet = AnimatorSet()
    private val cardViewReverseAnimSet = AnimatorSet()
    private val duration: Long = 400
    private val reverseDuration: Long = 200
    private val delayDuration: Long = 100
    private val translationX: Float = -700F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        initStatusBar()
        setupToolbar()

        App.component.inject(this)

        setupClickListeners()
        setupAnimations()
        setupReverseAnimation()
        setupStatisticsAnimation()
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
        clearStatisticView()
        presenter.getStatistics()
    }

    private fun initStatusBar() {
        val window = window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            val tintManager = SystemBarTintManager(this)
            tintManager.isStatusBarTintEnabled = true
            tintManager.setTintColor(ContextCompat.getColor(this, R.color.blue_grey_700))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = ContextCompat.getColor(this, R.color.blue_grey_700)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        val toggle = object : ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                startReverseAnimations()
            }
        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigation_view.setNavigationItemSelectedListener(this)
        navigation_view.getHeaderView(0)?.ivLogout?.onClick { toast("logout") }
        navigation_view.menu?.getItem(0)?.isChecked = true
    }

    private fun setupStatisticsAnimation() {
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
        statisticsAnimSet.duration = 1500
        statisticsAnimSet.startDelay = 100
        statisticsAnimSet.interpolator = DecelerateInterpolator()
    }

    private fun setupAnimations() {
        exam_card_view.visibility = View.INVISIBLE
        training_card_view.visibility = View.INVISIBLE
        chapters_card_view.visibility = View.INVISIBLE
        search_card_view.visibility = View.INVISIBLE
        favourite_card_view.visibility = View.INVISIBLE

        val exAnim = ObjectAnimator.ofFloat(exam_card_view, "translationX", translationX, 0F)
        exAnim.duration = duration
        exAnim.startDelay = delayDuration
        exAnim.interpolator = DecelerateInterpolator()
        exAnim.addUpdateListener { exam_card_view.visibility = View.VISIBLE }
        val exAlphaAnim = ValueAnimator.ofFloat(0F, 1F)
        exAlphaAnim.duration = duration
        exAlphaAnim.startDelay = delayDuration
        exAlphaAnim.addUpdateListener { exam_card_view.alpha = exAlphaAnim.animatedValue as Float }

        val trainAnim = ObjectAnimator.ofFloat(training_card_view, "translationX", translationX, 0F)
        trainAnim.duration = duration
        trainAnim.startDelay = delayDuration + 100
        trainAnim.interpolator = DecelerateInterpolator()
        trainAnim.addUpdateListener { training_card_view.visibility = View.VISIBLE }
        val trainAlphaAnim = ValueAnimator.ofFloat(0F, 1F)
        trainAlphaAnim.duration = duration
        trainAlphaAnim.startDelay = delayDuration + 100
        trainAlphaAnim.addUpdateListener { training_card_view.alpha = trainAlphaAnim.animatedValue as Float }

        val chaptersAnim = ObjectAnimator.ofFloat(chapters_card_view, "translationX", translationX, 0F)
        chaptersAnim.duration = duration
        chaptersAnim.startDelay = delayDuration + 200
        chaptersAnim.interpolator = DecelerateInterpolator()
        chaptersAnim.addUpdateListener { chapters_card_view.visibility = View.VISIBLE }
        val chaptersAlphaAnim = ValueAnimator.ofFloat(0F, 1F)
        chaptersAlphaAnim.duration = duration
        chaptersAlphaAnim.startDelay = delayDuration + 200
        chaptersAlphaAnim.addUpdateListener { chapters_card_view.alpha = chaptersAlphaAnim.animatedValue as Float }

        val searchAnim = ObjectAnimator.ofFloat(search_card_view, "translationX", translationX, 0F)
        searchAnim.duration = duration
        searchAnim.startDelay = delayDuration + 300
        searchAnim.interpolator = DecelerateInterpolator()
        searchAnim.addUpdateListener { search_card_view.visibility = View.VISIBLE }
        val searchAlphaAnim = ValueAnimator.ofFloat(0F, 1F)
        searchAlphaAnim.duration = duration
        searchAlphaAnim.startDelay = delayDuration + 300
        searchAlphaAnim.addUpdateListener { search_card_view.alpha = searchAlphaAnim.animatedValue as Float }

        val favAnim = ObjectAnimator.ofFloat(favourite_card_view, "translationX", translationX, 0F)
        favAnim.duration = duration
        favAnim.startDelay = delayDuration + 400
        favAnim.interpolator = DecelerateInterpolator()
        favAnim.addUpdateListener { favourite_card_view.visibility = View.VISIBLE }
        val favAlphaAnim = ValueAnimator.ofFloat(0F, 1F)
        favAlphaAnim.duration = duration
        favAlphaAnim.startDelay = delayDuration + 400
        favAlphaAnim.addUpdateListener { favourite_card_view.alpha = favAlphaAnim.animatedValue as Float }

        cardViewAnimSet.playTogether(exAnim, trainAnim, chaptersAnim, searchAnim, favAnim, exAlphaAnim, trainAlphaAnim, chaptersAlphaAnim, searchAlphaAnim, favAlphaAnim)
        cardViewAnimSet.startDelay = 200
        cardViewAnimSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
                showStatistics()
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {}

        })
        startAnimations()
    }

    private fun setupReverseAnimation() {
        val exAnim = ObjectAnimator.ofFloat(exam_card_view, "translationX", 0F, translationX)
        exAnim.duration = reverseDuration
        exAnim.startDelay = delayDuration
        exAnim.interpolator = DecelerateInterpolator()
        exAnim.addUpdateListener { exam_card_view.visibility = View.VISIBLE }
        val exRevAlphaAnim = ValueAnimator.ofFloat(1F, 0F)
        exRevAlphaAnim.duration = reverseDuration
        exRevAlphaAnim.startDelay = delayDuration
        exRevAlphaAnim.addUpdateListener { exam_card_view.alpha = exRevAlphaAnim.animatedValue as Float }

        val trainAnim = ObjectAnimator.ofFloat(training_card_view, "translationX", 0F, translationX)
        trainAnim.duration = reverseDuration
        trainAnim.startDelay = delayDuration + 100
        trainAnim.interpolator = DecelerateInterpolator()
        trainAnim.addUpdateListener { training_card_view.visibility = View.VISIBLE }
        val trainRevAlphaAnim = ValueAnimator.ofFloat(1F, 0F)
        trainRevAlphaAnim.duration = reverseDuration
        trainRevAlphaAnim.startDelay = delayDuration + 100
        trainRevAlphaAnim.addUpdateListener { training_card_view.alpha = trainRevAlphaAnim.animatedValue as Float }

        val chaptersAnim = ObjectAnimator.ofFloat(chapters_card_view, "translationX", 0F, translationX)
        chaptersAnim.duration = reverseDuration
        chaptersAnim.startDelay = delayDuration + 200
        chaptersAnim.interpolator = DecelerateInterpolator()
        chaptersAnim.addUpdateListener { chapters_card_view.visibility = View.VISIBLE }
        val chaptersRevAlphaAnim = ValueAnimator.ofFloat(1F, 0F)
        chaptersRevAlphaAnim.duration = reverseDuration
        chaptersRevAlphaAnim.startDelay = delayDuration +200
        chaptersRevAlphaAnim.addUpdateListener { chapters_card_view.alpha = chaptersRevAlphaAnim.animatedValue as Float }

        val searchAnim = ObjectAnimator.ofFloat(search_card_view, "translationX", 0F, translationX)
        searchAnim.duration = reverseDuration
        searchAnim.startDelay = delayDuration + 300
        searchAnim.interpolator = DecelerateInterpolator()
        searchAnim.addUpdateListener { search_card_view.visibility = View.VISIBLE }
        val searchRevAlphaAnim = ValueAnimator.ofFloat(1F, 0F)
        searchRevAlphaAnim.duration = reverseDuration
        searchRevAlphaAnim.startDelay = delayDuration + 300
        searchRevAlphaAnim.addUpdateListener { search_card_view.alpha = searchRevAlphaAnim.animatedValue as Float }

        val favAnim = ObjectAnimator.ofFloat(favourite_card_view, "translationX", 0F, translationX)
        favAnim.duration = reverseDuration
        favAnim.startDelay = delayDuration + 400
        favAnim.interpolator = DecelerateInterpolator()
        favAnim.addUpdateListener { favourite_card_view.visibility = View.VISIBLE }
        val favRevAlphaAnim = ValueAnimator.ofFloat(1F, 0F)
        favRevAlphaAnim.duration = reverseDuration
        favRevAlphaAnim.startDelay = delayDuration + 400
        favRevAlphaAnim.addUpdateListener { favourite_card_view.alpha = favRevAlphaAnim.animatedValue as Float }

        cardViewReverseAnimSet.playTogether(exAnim, trainAnim, chaptersAnim, searchAnim, favAnim, exRevAlphaAnim, trainRevAlphaAnim, chaptersRevAlphaAnim, searchRevAlphaAnim, favRevAlphaAnim)
        cardViewReverseAnimSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
                clearStatisticView()
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

    private fun clearStatisticView() {
        tvExamProgressCount.text = "Средний бал: 0"
        examProgressBar.progress = 0
        tvTrainingProgressCount.text = "В среднем 0 балов"
        trainingProgressBar.progress = 0
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

    override fun onPause() {
        super.onPause()
        statisticsAnimSet.cancel()
        presenter.unBind()
    }

    override fun showStatistics() {
        if (!statisticsAnimSet.isRunning) {
            statisticsAnimSet.start()
        }
    }

    override fun onClick(p0: View?) = when (p0?.id) {
        exam_Layout.id -> {
            startActivityForResult<TestingActivity>(1)
        }
        training_layout.id -> {
            presenter.onLayoutClick(0, 0, false)
        }
        chapters_layout.id -> {
            presenter.onLayoutClick(0, 2, false)
            startActivity<ChaptersActivity>()
        }
        search_layout.id -> {
            startActivity<SearchActivity>()
        }
        favourite_layout.id -> {
            presenter.onLayoutClick(0, 4, false)
        }
        else -> println("miss click")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        toast("Result Code $resultCode Request Code $requestCode")
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_base_exam -> {
//                toast("base")
                drawer.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_1_serial_exam -> {
//                toast("serial 1")
                drawer.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_2_serial_exam -> {
//                toast("serial 2")
                drawer.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_3_serial_exam -> {
//                toast("serial 3")
                drawer.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_4_serial_exam -> {
//                toast("serial 4")
                drawer.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_5_serial_exam -> {
//                toast("serial 5")
                drawer.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_6_serial_exam -> {
//                toast("serial 6")
                drawer.closeDrawer(GravityCompat.START)
                true
            }
            R.id.nav_7_serial_exam -> {
//                toast("serial 7")
                drawer.closeDrawer(GravityCompat.START)
                true
            }
            else -> return false
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
