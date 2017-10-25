package com.fintrainer.fintrainer.views.search

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.view.View
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.SearchAdapter
import com.fintrainer.fintrainer.di.contracts.SearchContract
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.views.App
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import javax.inject.Inject

class SearchActivity : AppCompatActivity(), SearchContract.View {

    @Inject
    lateinit var presenter: SearchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        App.initSearchComponent()?.inject(this)
        presenter.bind(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Поиск"

        search.queryHint = "Введите ключевое слово"
        search.setIconifiedByDefault(false)
        search.clearFocus()

        presenter.loadQuestions(intent.getIntExtra("examId", -1))
    }

    override fun showQuestions(questions: List<TestingDto>) {
        progressBar.visibility = View.GONE
        search.visibility = View.VISIBLE
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = SearchAdapter(intent.getIntExtra("intentId", -1), questions)

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                (recycler.adapter as SearchAdapter).filter.filter(query)
                recycler.layoutManager.scrollToPosition(0)
                search.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                (recycler.adapter as SearchAdapter).filter.filter(newText)
                recycler.layoutManager.scrollToPosition(0)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unBind()
    }
}
