package com.fintrainer.fintrainer.views.search

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.SearchAdapter
import com.fintrainer.fintrainer.adapters.SearchRecyclerAdapter
import com.fintrainer.fintrainer.di.contracts.SearchContract
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.utils.containers.PicassoContainer
import com.fintrainer.fintrainer.utils.header.StickyHeaderLayoutManager
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseActivity
import icepick.State
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import javax.inject.Inject


class SearchActivity : BaseActivity(), SearchContract.View {

    @Inject
    lateinit var presenter: SearchPresenter

    @Inject
    lateinit var picasso: PicassoContainer

    @State
    @JvmField
    var showFab: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        App.initSearchComponent()?.inject(this)
        presenter.bind(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.search)

        search.queryHint = getString(R.string.key_word)
        search.setIconifiedByDefault(false)
        search.clearFocus()

        presenter.loadQuestions(intent.getIntExtra("examId", -1))
    }

    override fun showQuestions(questions: List<TestingDto>) {

        progressBar.visibility = View.GONE
        search.visibility = View.VISIBLE
        recycler.layoutManager = StickyHeaderLayoutManager()
        recycler.adapter = SearchAdapter(intent.getIntExtra("intentId", -1), questions, picasso, this, object : SearchAdapter.FabVisibilityController {
            override fun fabState(showFab: Boolean) {
                this@SearchActivity.showFab = showFab
                if (!showFab) {
                    fab.visibility = View.GONE
                } else {
                    fab.show()
                }
            }
        })

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.visibility == View.VISIBLE) {
                    fab.hide()
                } else if (dy < 0 && fab.visibility != View.VISIBLE && showFab) {
                    fab.show()
                }
            }
        })

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
        chapters_recycler.adapter = SearchRecyclerAdapter(intent.getIntExtra("chaptersCount", 0), this, object : SearchRecyclerAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                (recycler.adapter as SearchAdapter).showCurrentChapter(position)
                recycler.scrollToPosition(0)
                search.setQuery("", false)
                search.clearFocus()
            }

        })
        chapters_recycler.clearFocus()

        fab.onClick {
            (recycler.adapter as SearchAdapter).showAllChapters()
            (chapters_recycler.adapter as SearchRecyclerAdapter).resetClickedChapter()
            chapters_recycler.smoothScrollToPosition(0)
            recycler.scrollToPosition(0)
            search.setQuery("", false)
            search.clearFocus()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        inflater!!.inflate(R.menu.search_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        R.id.menu_show_wrong -> {
            item.isChecked = !item.isChecked
            (recycler.adapter as SearchAdapter).changeWrongAnswersVisible(item.isChecked)
            (recycler.adapter as SearchAdapter).notifyAllSectionsDataSetChanged()
            true
        }
        else -> false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unBind()
    }
}
