package com.fintrainer.fintrainer.views.discussions.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.DiscussionsAdapter
import com.fintrainer.fintrainer.di.contracts.DiscussionsContract
import com.fintrainer.fintrainer.utils.Constants.DISCUSSIONS_FRAGMENT_TAG
import com.fintrainer.fintrainer.views.BaseFragment
import com.fintrainer.fintrainer.views.discussions.DiscussionsActivity
import kotlinx.android.synthetic.main.fragment_discussions.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by krotk on 23.10.2017.
 */
class FragmentDiscussions : BaseFragment(), DiscussionsContract.DiscussionsView {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val discussionActivity = activity as DiscussionsActivity
        discussionActivity.setSupportActionBar(toolbar)
        discussionActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupRecycler()
    }

    private fun setupRecycler() {
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = DiscussionsAdapter()
    }

    override fun showDiscussions() {

    }

    override fun getFragmentLayout(): Int = R.layout.fragment_discussions

    override fun getFragmentTag(): String = DISCUSSIONS_FRAGMENT_TAG

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId){
        android.R.id.home ->{
            activity.onBackPressed()
            true
        }
        else -> false
    }
}