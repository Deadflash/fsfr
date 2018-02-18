package com.fintrainer.fintrainer.views.search

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.view.View
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.SearchAdapter
import com.fintrainer.fintrainer.adapters.SearchRecyclerAdapter
import com.fintrainer.fintrainer.di.contracts.SearchContract
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.utils.containers.PicassoContainer
import com.fintrainer.fintrainer.utils.header.StickyHeaderLayoutManager
import com.fintrainer.fintrainer.views.App
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class SearchActivity : AppCompatActivity(), SearchContract.View {

    @Inject
    lateinit var presenter: SearchPresenter

    @Inject
    lateinit var picasso: PicassoContainer

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
        recycler.layoutManager = StickyHeaderLayoutManager()
        recycler.adapter = SearchAdapter(intent.getIntExtra("intentId", -1), questions, picasso, this)

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

        chapters_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        chapters_recycler.adapter = SearchRecyclerAdapter(13, this, object : SearchRecyclerAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                (recycler.adapter as SearchAdapter).showCurrentChapter(position)
                recycler.scrollToPosition(0)
            }

        })
        chapters_recycler.clearFocus()
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
