package com.fintrainer.fintrainer.views.search

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.SearchAdapter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Поиск"

        search.queryHint = "Введите ключевое слово"
        search.setIconifiedByDefault(false)
        search.clearFocus()

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = SearchAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }
}
