package com.fintrainer.fintrainer.views.discussions.fragments

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.adapters.CommentsAdapter
import com.fintrainer.fintrainer.di.contracts.DiscussionsContract
import com.fintrainer.fintrainer.structure.DiscussionQuestionDto
import com.fintrainer.fintrainer.utils.Constants.COMMENTS_FRAGMENT_TAG
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseFragment
import com.fintrainer.fintrainer.views.discussions.DiscussionsPresenter
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.fragment_comments.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

/**
 * Created by krotk on 23.10.2017.
 */
class FragmentComments : BaseFragment(), DiscussionsContract.CommentsView, CommentsAdapter.OnRateClick {

    @Inject
    lateinit var presenter: DiscussionsPresenter

    private lateinit var discussion: DiscussionQuestionDto

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.initDiscussionsComponent()?.inject(this)
        presenter.bindDiscussionsCommentsView(this)

        recycler.isNestedScrollingEnabled = false
        recycler.clearFocus()

        ivSend.onClick {
            if (!etMessage.text.toString().trim { it <= ' ' }.isEmpty()) {
                presenter.addComment(discussion, etMessage.text.toString())
                etMessage.text.clear()
                val imm = it?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
                nestedScrollView.fullScroll(View.FOCUS_DOWN)
                etMessage.clearFocus()
            } else {
                etMessage.error = getString(R.string.message_error_text)
            }
        }

        presenter.getDiscussionComments(arguments.getString("questionId"), arguments.getInt("questionType"), arguments.getString("realmId"))
    }

    override fun likeClick(position: Int) {

    }

    override fun dislikeClick(position: Int) {

    }

    override fun onCommentCreated() {
        presenter.getDiscussionComments(arguments.getString("questionId"), arguments.getInt("questionType"), arguments.getString("realmId"))
    }

    override fun showComments(discussion: DiscussionQuestionDto, account: GoogleSignInAccount?) {
        tvQuestion?.text = discussion.text
        this.discussion = discussion
        discussion.commentList?.let {
            if (!it.isEmpty()) {
                recycler.visibility = View.VISIBLE
                recycler.layoutManager = LinearLayoutManager(context)
                recycler.adapter = CommentsAdapter(account, it, this)
            } else {
                recycler.visibility = View.GONE
            }
        }
    }

    override fun showError() {
        recycler.visibility = View.GONE
        toast(R.string.something_went_wrong)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unbindDiscussionsCommentsView()
    }

    override fun getFragmentLayout(): Int = R.layout.fragment_comments

    override fun getFragmentTag(): String = COMMENTS_FRAGMENT_TAG
}