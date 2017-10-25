package com.fintrainer.fintrainer.views.chapters

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.ChaptersAdapter
import com.fintrainer.fintrainer.di.contracts.ChaptersContract
import com.fintrainer.fintrainer.structure.ChapterRealm
import com.fintrainer.fintrainer.structure.TestingDto
import com.fintrainer.fintrainer.utils.Constants.CHAPTER_INTENT
import com.fintrainer.fintrainer.utils.Constants.TESTING_INTENT
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseActivity
import com.fintrainer.fintrainer.views.testing.TestingActivity
import kotlinx.android.synthetic.main.activity_chapters.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject

class ChaptersActivity : BaseActivity(), ChaptersContract.View, ChaptersAdapter.OnChapterClick {

    @Inject
    lateinit var presenter: ChaptersPresenter

    private lateinit var tests: List<TestingDto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters)
        App.initChapterComponent()?.inject(this)

        presenter.bind(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "По Главам"

        presenter.loadChapters(intent.getIntExtra("examId", -1))
    }

    override fun showChapters(chapters: List<ChapterRealm>) {
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ChaptersAdapter(chapters,this)
        recycler.clearAnimation()
        recycler.visibility = View.VISIBLE
        recycler.animate()
    }

    override fun onChapterSelected(chapter: Int) {
        startActivityForResult<TestingActivity>(CHAPTER_INTENT,"intentId" to CHAPTER_INTENT ,"examId" to intent.getIntExtra("examId",0),"chapter" to chapter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHAPTER_INTENT){
            App.releaseTestingComponent()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId){
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }
}
