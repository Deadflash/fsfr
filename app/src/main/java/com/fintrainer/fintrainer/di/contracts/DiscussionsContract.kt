package com.fintrainer.fintrainer.di.contracts

import android.content.Intent
import com.fintrainer.fintrainer.structure.DiscussionCommentDto
import com.fintrainer.fintrainer.structure.DiscussionQuestionDto
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Created by krotk on 28.10.2017.
 */
interface DiscussionsContract {

    interface View : IView {
//        fun realmStatus(code: Int)
    }

    interface DiscussionsView : IView {
        fun showDiscussions(discussions: List<DiscussionQuestionDto>, account: GoogleSignInAccount?)
        fun realmStatus(code: Int)
        fun onSuccessRate()
    }

    interface CommentsView : IView {
        fun showComments(discussion: DiscussionQuestionDto, account: GoogleSignInAccount?)
        fun showError()
        fun onCommentCreated()
        fun onSuccessRate()
    }

    interface AddDiscussionView : IView {
        fun createDiscussionResult(code: Int)
        fun onCreateDiscussionClicked()
    }

    interface Presenter : IPresenter {
        fun bindDiscussionsView(iView: IView)
        fun unbindDiscussionsView()
        fun bindDiscussionsCommentsView(iView: IView)
        fun unbindDiscussionsCommentsView()
        fun bindAddDiscussionsView(iView: IView)
        fun unbindAddDiscussionsView()
        fun initDiscussionsRealm()
        fun handleAuthResult(requestCode: Int, resultCode: Int, data: Intent)

        fun addComment(discussion: DiscussionQuestionDto, comment: String)
        fun rateDiscussion(discussion: DiscussionQuestionDto, rate: Boolean)
        fun rateComment(comment: DiscussionCommentDto, rate: Boolean)
        fun getDiscussions(questionId: String, questionType: Int)
        fun getDiscussionComments(questionId: String, questionType: Int, realmId: String)
        fun onCreateDiscussionClicked()
        fun createDiscussion(text: String, questionCode: String, questionType: Int)
    }
}