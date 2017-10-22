package com.fcpunlimited.fsfr.views.testing

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import com.fcpunlimited.fsfr.R
import com.fcpunlimited.fsfr.views.BaseActivity
import com.fcpunlimited.fsfr.views.testing.fragments.TestingFragment
import kotlinx.android.synthetic.main.activity_testing.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.toast

class TestingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        view_pager.adapter = SectionsPagerAdapter(supportFragmentManager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.testing_menu, menu)
        return true
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
            toast("Favourite")
            true
        }
        R.id.menu_discussions -> {
            toast("Discussions")
            true
        }
        else -> false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = TestingFragment()

        override fun getCount(): Int = 300

        override fun getPageTitle(position: Int): CharSequence? = "TESTING"

    }
}
