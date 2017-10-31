package com.fintrainer.fintrainer.views.discussions.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.DiscussionsAdapter
import com.fintrainer.fintrainer.di.contracts.DiscussionsContract
import com.fintrainer.fintrainer.structure.DiscussionQuestionDto
import com.fintrainer.fintrainer.utils.Constants.DISCUSSIONS_FRAGMENT_TAG
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseFragment
import com.fintrainer.fintrainer.views.discussions.DiscussionsPresenter
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.fragment_discussions.*
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

/**
 * Created by krotk on 23.10.2017.
 */
class FragmentDiscussions : BaseFragment(), DiscussionsContract.DiscussionsView {

    @Inject
    lateinit var presenter: DiscussionsPresenter

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.initDiscussionsComponent()?.inject(this)

        val purchased = activity.intent.getBooleanExtra("purchased", false)
        val questionCode = activity.intent.getStringExtra("questionCode")
        val testType = activity.intent.getIntExtra("testType", -1)

        presenter.bindDiscussionsView(this)
        presenter.getDiscussions(questionCode, testType)
    }

    override fun showDiscussions(discussions: List<DiscussionQuestionDto>, account: GoogleSignInAccount?) {
        if (discussions.isEmpty()) {
            tvEmptyDiscussions.visibility = View.VISIBLE
            toast("Discussions is empty")
        } else {
            tvEmptyDiscussions.visibility = View.GONE
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = DiscussionsAdapter(discussions,account)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unbindDiscussionsView()
    }

    override fun getFragmentLayout(): Int = R.layout.fragment_discussions

    override fun getFragmentTag(): String = DISCUSSIONS_FRAGMENT_TAG
}