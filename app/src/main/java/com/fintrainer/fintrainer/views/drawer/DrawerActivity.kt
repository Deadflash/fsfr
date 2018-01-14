package com.fintrainer.fintrainer.views.drawer

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import collections.forEach
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.di.contracts.AuthContract
import com.fintrainer.fintrainer.di.contracts.DrawerContract
import com.fintrainer.fintrainer.structure.ExamStatisticAndInfo
import com.fintrainer.fintrainer.utils.Constants.APP_PNAME
import com.fintrainer.fintrainer.utils.Constants.CHAPTER_INTENT
import com.fintrainer.fintrainer.utils.Constants.EXAM_BASE
import com.fintrainer.fintrainer.utils.Constants.EXAM_INTENT
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_1
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_2
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_3
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_4
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_5
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_6
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_7
import com.fintrainer.fintrainer.utils.Constants.FAVOURITE_INTENT
import com.fintrainer.fintrainer.utils.Constants.RC_SIGN_IN
import com.fintrainer.fintrainer.utils.Constants.SEARCH_INTENT
import com.fintrainer.fintrainer.utils.Constants.TESTING_INTENT
import com.fintrainer.fintrainer.utils.containers.GoogleAuthContainer
import com.fintrainer.fintrainer.utils.containers.InAppPurchaseContainer
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseActivity
import com.fintrainer.fintrainer.views.chapters.ChaptersActivity
import com.fintrainer.fintrainer.views.search.SearchActivity
import com.fintrainer.fintrainer.views.testing.TestingActivity
import com.readystatesoftware.systembartint.SystemBarTintManager
import com.squareup.picasso.Picasso
import icepick.State
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.drawer_main.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import javax.inject.Inject


class DrawerActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerContract.View, View.OnClickListener, AuthContract.View, InAppPurchaseContainer.DrawerInterface {

    private val TAG = DrawerActivity::class.java.name

    @State
    @JvmField
    var examProgress: Int = 0

    @State
    @JvmField
    var trainingProgress: Int = 0

    @State
    @JvmField
    var chaptersCountProgress: Int = 0

    @State
    @JvmField
    var questionsCountProgress: Int = 0

    @State
    @JvmField
    var favouriteCountProgress: Int = 0

    @State
    @JvmField
    var selectedExam: Int = 0

    @State
    @JvmField
    var currentExam: Int = 0

    @State
    @JvmField
    var isAuthClicked: Boolean = false


    @Inject
    lateinit var presenter: DrawerPresenter

    @Inject
    lateinit var auth: GoogleAuthContainer

    @Inject
    lateinit var inAppPurchaseContainer: InAppPurchaseContainer

    private val statisticsAnimSet = AnimatorSet()
    private val cardViewAnimSet = AnimatorSet()
    private val cardViewReverseAnimSet = AnimatorSet()
    private val duration: Long = 400
    private val reverseDuration: Long = 200
    private val delayDuration: Long = 100
    private var translationX: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        App.initDrawerComponent()?.inject(this)

        val displayMetrics = DisplayMetrics()
        (applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        translationX = -displayMetrics.widthPixels.toFloat()

        initStatusBar()
        setupDrawer()

        setupNavigationView()

        setupClickListeners()
        setupCardViewAnimations()
        setupReverseCardViewAnimations()

        setupUserProgress()
        presenter.bind(this)
        presenter.getStatistics(selectedExam, true)
    }

    override fun onStart() {
        super.onStart()
        auth.bind(this@DrawerActivity)
        auth.tryToLogin()
    }

    override fun onResume() {
        super.onResume()
        clearStatisticView()
        inAppPurchaseContainer.initDrawer(this)
        if (intent.getBooleanExtra("buy",false)){
            intent.removeExtra("buy")
            inAppPurchaseContainer.purchase(this, selectedExam)
            App.releaseChapterComponent()
            App.releaseTestingComponent()
        }
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

    private fun setupDrawer() {
        setSupportActionBar(toolbar)
        val toggle = object : ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                if (currentExam != selectedExam) {
                    currentExam = selectedExam
                    cardViewReverseAnimSet.start()
                    presenter.getStatistics(selectedExam, true)
                    setupTitle()
                }
            }
        }
        toggle.isDrawerSlideAnimationEnabled = false
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        setupTitle()
    }

    private fun setupNavigationView() {

        navigation_view.setNavigationItemSelectedListener(this)
        val navView = navigation_view.getHeaderView(0)
        navView?.ivLogout?.isClickable = !isAuthClicked
        navView?.ivLogin?.isClickable = !isAuthClicked
        navView?.ivLogout?.onClick {
            auth.logout()
            isAuthClicked = true
            navView.ivLogout?.isClickable = false
        }
        navView?.ivLogin?.onClick {
            auth.login()
            isAuthClicked = true
            navView.ivLogin?.isClickable = false
        }
        navigation_view.menu?.getItem(0)?.isChecked = true

        setupPurchasedIcons()
    }

    fun setupPurchasedIcons() {
        val purchases = inAppPurchaseContainer.getPurchases()
        purchases.forEach(action = { i, purchaseStructDto ->
            run {
                if (purchaseStructDto.hasPurchased) {
                    navigation_view.menu?.getItem(i)?.setIcon(R.drawable.ic_check_black_24dp)
                    navigation_view.menu?.getItem(i)?.icon?.setColorFilter(ContextCompat.getColor(this@DrawerActivity, R.color.green_300), PorterDuff.Mode.SRC_IN)
                } else {
                    navigation_view.menu?.getItem(i)?.setIcon(R.drawable.ic_shopping_cart_black_24dp)
                }
            }
        })
    }

    override fun setLoginLogoutButtonsClickable() {
        navigation_view.getHeaderView(0)?.ivLogout?.isClickable = true
        navigation_view.getHeaderView(0)?.ivLogin?.isClickable = true
    }

    override fun showUserInfo(userName: String, userAvatarUri: Uri, isLoggedIn: Boolean) {
        isAuthClicked = false
        val navView = navigation_view.getHeaderView(0)
        navView?.tvName?.text = userName
//        navView?.ivAvatar?.setImageURI(userAvatarUri)
        if (userAvatarUri != Uri.EMPTY) {
            Picasso.with(this)
                    .load(userAvatarUri)
                    .fit()
                    .centerInside()
                    .error(R.mipmap.ic_launcher)
                    .into(navView?.ivAvatar)
        } else {
            navView?.ivAvatar?.setImageResource(R.mipmap.ic_launcher)
        }
//        navView.ivLogout.setImageResource(if (isLoggedIn) R.drawable.ic_account_box_white_24dp else R.drawable.ic_exit_to_app_white_24dp)
        if (isLoggedIn) {
            navView?.ivLogout?.visibility = View.VISIBLE
            navView?.ivLogin?.visibility = View.INVISIBLE
            navView?.ivLogin?.isClickable = false
            navView?.ivLogout?.isClickable = true
        } else {
            navView?.ivLogout?.visibility = View.INVISIBLE
            navView?.ivLogin?.visibility = View.VISIBLE
            navView?.ivLogin?.isClickable = true
            navView?.ivLogout?.isClickable = false
        }
    }

    private fun setupTitle() {
        supportActionBar?.title = when (selectedExam) {
            EXAM_BASE -> "Базовый экзамен"
            EXAM_SERIAL_1 -> "Серия 1.0"
            EXAM_SERIAL_2 -> "Серия 2.0"
            EXAM_SERIAL_3 -> "Серия 3.0"
            EXAM_SERIAL_4 -> "Серия 4.0"
            EXAM_SERIAL_5 -> "Серия 5.0"
            EXAM_SERIAL_6 -> "Серия 6.0"
            EXAM_SERIAL_7 -> "Серия 7.0"
            else -> "ФСФР Экзамены"
        }
    }

    private fun setupStatisticsAnimation(examProgress: Int, trainingProgress: Int, chaptersCount: Int, questionsCount: Int, favouriteCount: Int) {
        val exam = ObjectAnimator.ofInt(examProgressBar, "progress", 0, examProgress)
        exam.addUpdateListener {
            tvExamProgressCount?.text = "Средний бал: ${exam.animatedValue}"
        }
        val train = ObjectAnimator.ofInt(trainingProgressBar, "progress", 0, trainingProgress)
        train.addUpdateListener {
            tvTrainingProgressCount?.text = "В среднем ${train.animatedValue} балов"
        }
        val chaptersCountAnim = ValueAnimator.ofInt(0, chaptersCount)
        chaptersCountAnim.addUpdateListener {
            tvChaptersProgressCount.text = "Глав: ${chaptersCountAnim.animatedValue}"
        }
        val questionsCountAnim = ValueAnimator.ofInt(0, questionsCount)
        questionsCountAnim.addUpdateListener {
            tvQuestionsProgressCount.text = "Всего вопросов: ${questionsCountAnim.animatedValue}"
        }
        val favouriteCountAnim = ValueAnimator.ofInt(0, favouriteCount)
        favouriteCountAnim.addUpdateListener {
            tvFavouriteProgressCount.text = "Добавлено ${favouriteCountAnim.animatedValue} вопросов"
        }
        statisticsAnimSet.playTogether(exam, train, chaptersCountAnim, questionsCountAnim, favouriteCountAnim)
        statisticsAnimSet.duration = 1500
        statisticsAnimSet.startDelay = 150
        statisticsAnimSet.interpolator = DecelerateInterpolator()
    }

    private fun setupCardViewAnimations() {
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
                statisticsAnimSet.start()
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {
                clearStatisticView()
            }

        })
    }

    private fun setupReverseCardViewAnimations() {
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
        chaptersRevAlphaAnim.startDelay = delayDuration + 200
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
//                cardViewAnimSet.start()
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {}

        })
    }

    override fun refreshStatistics() {
        statisticsAnimSet.start()
    }

    private fun clearStatisticView() {
        tvExamProgressCount.text = "Средний бал: 0"
        examProgressBar.progress = 0
        tvTrainingProgressCount.text = "В среднем 0 балов"
        trainingProgressBar.progress = 0
        tvChaptersProgressCount.text = "Глав: 0"
        tvQuestionsProgressCount.text = "Всего вопросов: 0"
        tvFavouriteProgressCount.text = "Добавлено 0 вопросов"
    }

    private fun setupUserProgress() {
        tvExamProgressCount.text = "Средний бал: ${examProgress.toString() ?: "0"}"
        examProgressBar.progress = examProgress ?: 0
        tvTrainingProgressCount.text = "В среднем ${trainingProgress.toString() ?: "0"} балов"
        trainingProgressBar.progress = trainingProgress ?: 0
        tvChaptersProgressCount.text = "Глав: ${chaptersCountProgress.toString() ?: "0"}"
        tvQuestionsProgressCount.text = "Всего вопросов: ${questionsCountProgress.toString() ?: "0"}"
        tvFavouriteProgressCount.text = "Добавлено ${favouriteCountProgress.toString() ?: "0"} вопросов"
    }

    private fun setupClickListeners() {
        exam_Layout.setOnClickListener(this)
        training_layout.setOnClickListener(this)
        chapters_layout.setOnClickListener(this)
        search_layout.setOnClickListener(this)
        favourite_layout.setOnClickListener(this)
    }

    override fun showStatistics(statistics: ExamStatisticAndInfo, showFullAnim: Boolean) {
        favouriteCountProgress = statistics.favouriteQuestionsCount
        examProgress = statistics.averageGrade
        trainingProgress = statistics.averageRightAnswers
        chaptersCountProgress = statistics.chaptersCount
        questionsCountProgress = statistics.questionsCount
        setupStatisticsAnimation(statistics.averageGrade, statistics.averageRightAnswers, statistics.chaptersCount, statistics.questionsCount, statistics.favouriteQuestionsCount)
        if (!cardViewAnimSet.isRunning) {
            if (showFullAnim) {
                cardViewAnimSet.start()
            } else {
                statisticsAnimSet.start()
            }
        }
//        startStatisticAnimation()
    }

    override fun onClick(p0: View?) {
        val id = p0?.id
        id?.let {
            if (it == exam_Layout.id || inAppPurchaseContainer.checkIsPurchasedExam(selectedExam)) {
                showExam(it)
            } else {
                showTrialDialog(getString(R.string.trial_message), it)
            }
        }
    }

    private fun showExam(id: Int) {
        when (id) {
            exam_Layout.id -> {
                startActivityForResult<TestingActivity>(EXAM_INTENT, "intentId" to EXAM_INTENT, "examId" to currentExam)
            }
            training_layout.id -> {
                startActivityForResult<TestingActivity>(TESTING_INTENT, "intentId" to TESTING_INTENT, "examId" to currentExam)
            }
            chapters_layout.id -> {
//            presenter.onLayoutClick(0, 2, false)
                startActivityForResult<ChaptersActivity>(CHAPTER_INTENT, "intentId" to CHAPTER_INTENT, "examId" to currentExam)
            }
            search_layout.id -> {
                startActivityForResult<SearchActivity>(SEARCH_INTENT, "intentId" to SEARCH_INTENT, "examId" to currentExam)
            }
            favourite_layout.id -> {
                if (favouriteCountProgress == 0) {
                    val snack = Snackbar.make(findViewById(R.id.main_drawer_layout), "Нет избранных вопросов", Snackbar.LENGTH_SHORT)
                    snack.view.findViewById<TextView>(android.support.design.R.id.snackbar_text).setTextColor(Color.WHITE)
                    snack.show()
                } else {
                    startActivityForResult<TestingActivity>(FAVOURITE_INTENT, "intentId" to FAVOURITE_INTENT, "examId" to currentExam)
                }
            }
            else -> print("Miss click")
        }
    }

    private fun showTrialDialog(message: String, id: Int?) {
        val dialog = alert(message, getString(R.string.trial_version))
        dialog.positiveButton(getString(R.string.buy), onClicked = {
            inAppPurchaseContainer.purchase(this, selectedExam)
        })
        id?.let { if (it != search_layout.id && it != favourite_layout.id) dialog.negativeButton(getString(R.string.cancel), onClicked = { id.let { it -> showExam(it) } }) }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            EXAM_INTENT -> {
                App.releaseTestingComponent()
                presenter.getStatistics(selectedExam, false)
            }
            TESTING_INTENT -> {
                App.releaseTestingComponent()
                presenter.getStatistics(selectedExam, false)
            }
            CHAPTER_INTENT -> {
                App.releaseChapterComponent()
                App.releaseTestingComponent()
                presenter.getStatistics(selectedExam, false)
            }
            SEARCH_INTENT -> {
                App.releaseSearchComponent()
                presenter.getStatistics(selectedExam, false)
            }
            FAVOURITE_INTENT -> {
                App.releaseTestingComponent()
                presenter.getStatistics(selectedExam, false)
            }
            RC_SIGN_IN -> {
                if (resultCode == Activity.RESULT_CANCELED) {
                    toast(R.string.action_canceled)
                    setLoginLogoutButtonsClickable()
                } else {
                    auth.onAuthResult(requestCode, resultCode, data!!)
                }
            }
            10001 -> {
                data?.let { inAppPurchaseContainer.handlePurchaseResult(requestCode, resultCode, it) }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showPurchaseMessage() {
        toast("Уже куплено")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer.closeDrawer(GravityCompat.START)
        return when (item.itemId) {
            R.id.nav_base_exam -> {
                selectedExam = EXAM_BASE
                true
            }
            R.id.nav_1_serial_exam -> {
                selectedExam = EXAM_SERIAL_1
                true
            }
            R.id.nav_2_serial_exam -> {
                selectedExam = EXAM_SERIAL_2
                true
            }
            R.id.nav_3_serial_exam -> {
                selectedExam = EXAM_SERIAL_3
                true
            }
            R.id.nav_4_serial_exam -> {
                selectedExam = EXAM_SERIAL_4
                true
            }
            R.id.nav_5_serial_exam -> {
                selectedExam = EXAM_SERIAL_5
                true
            }
            R.id.nav_6_serial_exam -> {
                selectedExam = EXAM_SERIAL_6
                true
            }
            R.id.nav_7_serial_exam -> {
                selectedExam = EXAM_SERIAL_7
                true
            }
            R.id.nav_mail -> {
                email(getString(R.string.fcp_mail), "Вопрос", "")
                true
            }
            R.id.nav_favourite -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_url) + APP_PNAME)))
                true
            }
            R.id.nav_options -> {
                true
            }
            else -> return false
        }
    }

    override fun onPause() {
        super.onPause()
        statisticsAnimSet.cancel()
        inAppPurchaseContainer.releaseDrawer()
    }

    override fun onStop() {
        super.onStop()
        auth.unBind()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unBind()
        App.releaseDrawerComponent()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
