package com.fintrainer.fintrainer.views.testing

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.di.contracts.TestingContract
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.structure.TestingResultsDto
import com.fintrainer.fintrainer.utils.Constants.RESULT_INTENT
import com.fintrainer.fintrainer.utils.CustomViewPagerScroller
import com.fintrainer.fintrainer.utils.IPageSelector
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseActivity
import com.fintrainer.fintrainer.views.discussions.DiscussionsActivity
import com.fintrainer.fintrainer.views.result.ResultActivity
import com.fintrainer.fintrainer.views.testing.fragments.TestingFragment
import kotlinx.android.synthetic.main.activity_testing.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.lang.reflect.Field
import javax.inject.Inject

class TestingActivity : BaseActivity(), TestingContract.View, IPageSelector {

    @Inject
    lateinit var presenter: TestingPresenter

    private lateinit var tests: List<TestingDto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)
        App.initTestingComponent()?.inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.bind(this)
        presenter.loadTests(intent.getIntExtra("examId", -1), intent.getIntExtra("intentId", -1))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.testing_menu, menu)
        return true
    }

    override fun showTest(tests: List<TestingDto>) {
        this.tests = tests
        setupViewPager()
    }

    override fun showResults(results: TestingResultsDto) {
        startActivityForResult<ResultActivity>(RESULT_INTENT,"weight" to results.weight,
                "right" to results.right,"wrong" to results.wrong,"worthChapter" to results.worthChapter,
                "intentId" to intent.getIntExtra("intentId", -1), "standaloned" to intent.getBooleanExtra("standaloned", false),
                "testType" to (tests[0]?.type ?: 0),"purchased" to intent.getBooleanExtra("purchased", false))
    }

    private fun setupViewPager() {
        view_pager.adapter = SectionsPagerAdapter(supportFragmentManager)
        try {
            val mScroller: Field = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            val scroller = CustomViewPagerScroller(view_pager.context, LinearInterpolator())
            mScroller.set(view_pager, scroller)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        progressBar.visibility = View.GONE
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.getItem(1)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark_border_white_24dp)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        R.id.menu_favourite -> {
            true
        }
        R.id.menu_discussions -> {
            startActivity<DiscussionsActivity>()
            true
        }
        else -> false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val fragment = TestingFragment()
            val bundle = Bundle()
            bundle.putInt("position", position)
            fragment.arguments = bundle
            return fragment
        }

        override fun getCount(): Int = tests.size

        override fun getPageTitle(position: Int): CharSequence? = "TESTING"
    }

    override fun changePage(position: Int) {
        view_pager.setCurrentItem(position,true)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unBind()
    }
}

