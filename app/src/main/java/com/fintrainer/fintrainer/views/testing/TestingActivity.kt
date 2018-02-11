package com.fintrainer.fintrainer.views.testing

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.TestingTabbarAdapter
import com.fintrainer.fintrainer.di.contracts.TestingContract
import com.fintrainer.fintrainer.structure.DiscussionCommentDto
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.structure.TestingResultsDto
import com.fintrainer.fintrainer.utils.Constants.CHAPTER_INTENT
import com.fintrainer.fintrainer.utils.Constants.DISCUSSION_INTENT
import com.fintrainer.fintrainer.utils.Constants.EXAM_INTENT
import com.fintrainer.fintrainer.utils.Constants.FAILED_TESTS_INTENT
import com.fintrainer.fintrainer.utils.Constants.FAVOURITE_INTENT
import com.fintrainer.fintrainer.utils.Constants.RESULT_INTENT
import com.fintrainer.fintrainer.utils.Constants.TESTING_INTENT
import com.fintrainer.fintrainer.utils.CustomViewPagerScroller
import com.fintrainer.fintrainer.utils.IPageSelector
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseActivity
import com.fintrainer.fintrainer.views.discussions.DiscussionsActivity
import com.fintrainer.fintrainer.views.result.ResultActivity
import com.fintrainer.fintrainer.views.testing.fragments.TestingFragment
import icepick.State
import kotlinx.android.synthetic.main.activity_testing.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.lang.reflect.Field
import javax.inject.Inject

class TestingActivity : BaseActivity(), TestingContract.View, IPageSelector {

    @Inject
    lateinit var presenter: TestingPresenter

    private var tests: List<TestingDto>? = null
    private var hints: List<DiscussionCommentDto>? = null
    private var menuItem: MenuItem? = null
    private lateinit var sharedPreferences: SharedPreferences

    @State
    @JvmField
    var currentPagerPosition: Int = 0

    @State
    @JvmField
    var isFavourite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        App.initTestingComponent()?.inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        when (intent.getIntExtra("intentId", -1)) {
            EXAM_INTENT -> supportActionBar?.title = "Экзамен"
            TESTING_INTENT -> supportActionBar?.title = "Тренировка"
            CHAPTER_INTENT -> supportActionBar?.title = "Глава ${intent.getIntExtra("chapter", 1)}"
            FAILED_TESTS_INTENT -> supportActionBar?.title = "Ошибки"
            FAVOURITE_INTENT -> supportActionBar?.title = "Избранное"
        }

        presenter.bind(this)
        presenter.loadTests(intent.getIntExtra("examId", -1), intent.getIntExtra("intentId", -1), intent.getIntExtra("chapter", 1), intent.getBooleanExtra("purchased", false))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.testing_menu, menu)
        return true
    }

    override fun showNeedAuth() {
        val dialog = alert(getString(R.string.need_auth_message), getString(R.string.authentication))
        dialog.okButton { }
        dialog.show()
    }

    override fun showTest(tests: List<TestingDto>) {
        this.tests = tests
        if (tests.isEmpty()) {
            toast(R.string.something_went_wrong)
        } else {
            checkIsFavourite(false)
            setupViewPager()
        }
    }

    override fun showHints(hints: List<DiscussionCommentDto>) {
        this.hints = hints
        toast("hints ${hints.size}")
    }

    override fun showIsFavouriteQuestion(isFavourite: Boolean) {
        this.isFavourite = isFavourite
        setFavouriteIcon(isFavourite)
    }

    private fun setFavouriteIcon(isFavourite: Boolean) {
        menuItem?.setIcon(if (isFavourite) R.drawable.ic_bookmark_white_24dp else R.drawable.ic_bookmark_border_white_24dp)
    }

    override fun showResults(results: TestingResultsDto) {
        if (tests != null) {
            val dialog = alert("Вы ответили на все вопросы! Показать результаты?", "Показать результаты")
            dialog.positiveButton("Результаты", onClicked = {
                if (intent.getIntExtra("intentId", -1) == CHAPTER_INTENT) {
                    presenter.deleteChapterStatistics(tests!![0].type!!, tests!![0].chapter!!)
                }
                finish()
                startActivityForResult<ResultActivity>(RESULT_INTENT, "weight" to results.weight,
                        "right" to results.right, "wrong" to results.wrong, "worthChapter" to results.worthChapter,
                        "intentId" to intent.getIntExtra("intentId", -1), "standaloned" to intent.getBooleanExtra("standaloned", false),
                        "testType" to (tests!![0].type
                                ?: 0), "purchased" to intent.getBooleanExtra("purchased", false))
            })
            dialog.negativeButton("Позже", onClicked = {})
            dialog.show()
        }
    }

    private fun setupViewPager() {
        recycler.visibility = View.VISIBLE
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler.adapter = tests?.let {
            TestingTabbarAdapter(it, object : TestingTabbarAdapter.OnItemClick {
                override fun onTabClicked(position: Int) {
                    changePage(position)
                }
            }, intent.getIntExtra("intentId", -1))
        }

        view_pager.adapter = SectionsPagerAdapter(supportFragmentManager)
        try {
            val mScroller: Field = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            val scroller = CustomViewPagerScroller(view_pager.context, LinearInterpolator())
            mScroller.set(view_pager, scroller)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                currentPagerPosition = position
                checkIsFavourite(false)
                (recycler.adapter as? TestingTabbarAdapter)?.setSelectedPosition(position)
                recycler.adapter.notifyDataSetChanged()
                recycler.scrollToPosition(position)
            }

        })

        progressBar.visibility = View.GONE
    }

    private fun checkIsFavourite(isAddRemoveAction: Boolean) {
        if (tests != null) {
            presenter.checkIsQuestionFavourite(tests!![currentPagerPosition].index!!.toInt(), tests!![currentPagerPosition].type!!.toInt(), tests!![currentPagerPosition].chapter!!, isAddRemoveAction)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (intent.getIntExtra("intentId", -1) == EXAM_INTENT) {
            menu?.getItem(0)?.isVisible = false
            menu?.getItem(1)?.isVisible = false
        }
        menuItem = menu!!.getItem(1)
        setFavouriteIcon(isFavourite)
//        menu.getItem(1).icon = ContextCompat.getDrawable(this, if (isFavourite) R.drawable.ic_bookmark_white_24dp else R.drawable.ic_bookmark_border_white_24dp)
        return super.onPrepareOptionsMenu(menu)
    }

    fun autoAddToFavourite() {
        if (sharedPreferences.getBoolean("key_add_wrong_answers_to_favourite", false)) {
            checkIsFavourite(true)
        }
    }

    fun autoRemoveFromFavourite() {
        if (sharedPreferences.getBoolean("key_remove_right_answers_from_favourite", false)) {
            presenter.removeFromFavourite(tests!![currentPagerPosition].type!!.toInt(), tests!![currentPagerPosition].index!!.toInt())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        R.id.menu_favourite -> {
            checkIsFavourite(true)
            true
        }
        R.id.menu_discussions -> {

            if (presenter.checkIsAuthenticatedUser()) {
                startActivityForResult<DiscussionsActivity>(DISCUSSION_INTENT,
                        "purchased" to intent.getBooleanExtra("purchased", false),
                        "questionCode" to (tests?.get(currentPagerPosition)?.code.toString()),
                        "testType" to (tests?.get(currentPagerPosition)?.type ?: -1))
            } else {
                showNeedAuth()
            }
            true
        }
        else -> false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DISCUSSION_INTENT -> {
                App.releaseDiscussionsComponent()
            }
        }
    }

    override fun onBackPressed() {
        if (intent.getIntExtra("intentId", -1) != CHAPTER_INTENT && intent.getIntExtra("intentId", -1) != FAILED_TESTS_INTENT) {
            val dialog = alert("Если вы выйдете из теста, то весь текущий прогресс будет потерян!", "Выход")
            dialog.positiveButton("выйти", onClicked = {
                super.onBackPressed()
                setResult(Activity.RESULT_OK)
            })
            dialog.negativeButton("остаться", onClicked = {})
            dialog.show()
        } else {
            super.onBackPressed()
            setResult(Activity.RESULT_OK)
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val fragment = TestingFragment()
            val bundle = Bundle()
            bundle.putInt("position", position)
            fragment.arguments = bundle
            return fragment
        }

        override fun getCount(): Int = tests?.size ?: 0

        override fun getPageTitle(position: Int): CharSequence? = "TESTING"
    }

    override fun changePage(position: Int) {
        view_pager.setCurrentItem(position, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unBind()
    }
}