package com.fintrainer.fintrainer.views.chapters

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.ChaptersAdapter
import com.fintrainer.fintrainer.views.BaseActivity
import kotlinx.android.synthetic.main.activity_chapters.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ChaptersActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "По Главам"

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ChaptersAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId){
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }
}
