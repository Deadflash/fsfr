package com.fintrainer.fintrainer.views.discussions.fragments

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.DiscussionsAdapter
import com.fintrainer.fintrainer.di.contracts.DiscussionsContract
import com.fintrainer.fintrainer.structure.DiscussionQuestionDto
import com.fintrainer.fintrainer.utils.Constants.DISCUSSIONS_FRAGMENT_TAG
import com.fintrainer.fintrainer.utils.Constants.REALM_AUTH_TOKEN_ERROR
import com.fintrainer.fintrainer.utils.Constants.REALM_FAIL_CONNECT_CODE
import com.fintrainer.fintrainer.utils.Constants.REALM_SUCCESS_CONNECT_CODE
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseFragment
import com.fintrainer.fintrainer.views.discussions.DiscussionsActivity
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.initDiscussionsComponent()?.inject(this)

//        val purchased = activity!!.intent.getBooleanExtra("purchased", false)
//        val questionCode = activity!!.intent.getStringExtra("questionCode")
//        val testType = activity!!.intent.getIntExtra("testType", -1)

        presenter.bindDiscussionsView(this)
        presenter.initDiscussionsRealm()
    }

    override fun realmStatus(code: Int) = when (code) {
        Activity.RESULT_CANCELED -> {
            toast("canceled")
        }
        REALM_SUCCESS_CONNECT_CODE -> {
            presenter.getDiscussions(activity!!.intent.getStringExtra("questionCode"), activity!!.intent.getIntExtra("testType", -1))
        }
        REALM_FAIL_CONNECT_CODE -> {
            toast(R.string.error_realm_config)
        }
        REALM_AUTH_TOKEN_ERROR -> {
            toast("token error")
        }
        else -> {
            toast(R.string.something_went_wrong)
        }
    }

    override fun onSuccessRate() {
        recycler.adapter.notifyDataSetChanged()
    }

    override fun showDiscussions(discussions: List<DiscussionQuestionDto>, account: GoogleSignInAccount?) {
        if (discussions.isEmpty()) {
            tvEmptyDiscussions.visibility = View.VISIBLE
            recycler.visibility = View.GONE
//            toast("Discussions is empty")
        } else {
            tvEmptyDiscussions.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = DiscussionsAdapter(discussions, account, object : DiscussionsAdapter.DiscussionsButtonsClickListener {
                override fun onRateClick(discussionQuestionDto: DiscussionQuestionDto, rate: Boolean) {
                    presenter.rateDiscussion(discussionQuestionDto,rate)
                }

                override fun onClick(questionId: String, questionType: Int, realmId: String) {
                    val discussionsCommentsFragment = FragmentComments()
                    val bundle = Bundle()
                    bundle.putString("questionId", questionId)
                    bundle.putInt("questionType", questionType)
                    bundle.putString("realmId", realmId)
                    discussionsCommentsFragment.arguments = bundle
                    (activity as DiscussionsActivity).replaceFragment(discussionsCommentsFragment)
                    (activity as DiscussionsActivity).setMenuIconsVisibility(discussionsCommentsFragment)
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unbindDiscussionsView()
    }

    override fun getFragmentLayout(): Int = R.layout.fragment_discussions

    override fun getFragmentTag(): String = DISCUSSIONS_FRAGMENT_TAG
}