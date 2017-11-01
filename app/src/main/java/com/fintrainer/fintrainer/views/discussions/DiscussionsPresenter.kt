package com.fintrainer.fintrainer.views.discussions

import android.content.Intent
import com.fintrainer.fintrainer.di.contracts.DiscussionsContract
import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.structure.DiscussionCommentDto
import com.fintrainer.fintrainer.structure.DiscussionQuestionDto
import com.fintrainer.fintrainer.utils.Constants.REALM_ERROR_CODE
import com.fintrainer.fintrainer.utils.Constants.REALM_SUCCES_CODE
import com.fintrainer.fintrainer.utils.containers.DiscussionsSyncRealmContainer
import com.fintrainer.fintrainer.utils.containers.GoogleAuthContainer
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentAddDiscussion
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentComments
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentDiscussions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Created by krotk on 28.10.2017.
 */
class DiscussionsPresenter(private val discussionsSyncRealmContainer: DiscussionsSyncRealmContainer,
                           private val authContainer: GoogleAuthContainer)
    : DiscussionsContract.Presenter, DiscussionsSyncRealmContainer.DiscussionRealmCallBack {

    private var activityView: DiscussionsContract.View? = null
    private var discussionsView: DiscussionsContract.DiscussionsView? = null
    private var commentsView: DiscussionsContract.CommentsView? = null
    private var addDiscussionsView: DiscussionsContract.AddDiscussionView? = null
    private var account: GoogleSignInAccount? = null

    override fun bind(iView: IView) {
        activityView = iView as? DiscussionsActivity
        authContainer.bindDiscussionActivityView(iView as DiscussionsActivity)
    }

    override fun bindDiscussionsView(iView: IView) {
        discussionsView = iView as? FragmentDiscussions
    }

    override fun bindDiscussionsCommentsView(iView: IView) {
        commentsView = iView as? FragmentComments
    }

    override fun bindAddDiscussionsView(iView: IView) {
        addDiscussionsView = iView as? FragmentAddDiscussion
    }

    override fun initDiscussionsRealm() {
        authContainer.getAccount(object : GoogleAuthContainer.AccountCallback {
            override fun onError(code: Int) {
                discussionsView?.realmStatus(code)
            }

            override fun onSuccess(account: GoogleSignInAccount?) {
                account?.let {
                    this@DiscussionsPresenter.account = it
                    discussionsSyncRealmContainer.initDiscussionsRealm(it, this@DiscussionsPresenter)
                }
//                account?.let { realmContainer.createRealm() }
            }
        })
    }

    override fun realmConfigCallback(code: Int) {
        discussionsView?.realmStatus(code)
    }

    override fun onCreateDiscussionClicked() {
        addDiscussionsView?.onCreateDiscussionClicked()
    }

    override fun addComment(discussion: DiscussionQuestionDto, comment: String) {
        account?.let {
            if (it.displayName != null) {
                discussionsSyncRealmContainer.addComment(discussion, comment, it, object : DiscussionsSyncRealmContainer.RealmCallback {
                    override fun success() {
                        commentsView?.onCommentCreated()
                    }

                    override fun error(code: Int) {
                        commentsView?.showError()
                    }

                })
            }
        }
    }

    override fun rateDiscussion(discussion: DiscussionQuestionDto, rate: Boolean) {
        account?.let {
            discussionsSyncRealmContainer.rateDiscussion(discussion, it, rate, object : DiscussionsSyncRealmContainer.RealmCallback {
                override fun success() {
                    discussionsView?.onSuccessRate()
                }

                override fun error(code: Int) {

                }
            })
        }
    }

    override fun rateComment(comment: DiscussionCommentDto, rate: Boolean) {
        account?.let {
            discussionsSyncRealmContainer.rateComment(comment, it, rate,object : DiscussionsSyncRealmContainer.RealmCallback{
                override fun success() {
                    commentsView?.onSuccessRate()
                }

                override fun error(code: Int) {

                }
            })
        }
    }

    override fun createDiscussion(text: String, questionCode: String, questionType: Int) {
        account?.let {
            it.displayName?.let {
                discussionsSyncRealmContainer.createDiscussion(it, text, questionCode, questionType, object : DiscussionsSyncRealmContainer.RealmCallback {
                    override fun success() {
                        addDiscussionsView?.createDiscussionResult(REALM_SUCCES_CODE)
                    }

                    override fun error(code: Int) {
                        addDiscussionsView?.createDiscussionResult(REALM_ERROR_CODE)
                    }

                })
            }
        }
    }

    fun closeRealm() {
        discussionsSyncRealmContainer.closeDiscussionRealm()
    }

    override fun getDiscussions(questionId: String, questionType: Int) {
        discussionsSyncRealmContainer.getDiscussions(questionId, questionType, object : DiscussionsSyncRealmContainer.DiscussionsCallback {
            override fun handleDiscussions(discussions: List<DiscussionQuestionDto>) {
                discussionsView?.showDiscussions(discussions, account)
            }
        })
    }

    override fun getDiscussionComments(questionId: String, questionType: Int, realmId: String) {
        discussionsSyncRealmContainer.getDiscussionComments(questionId, questionType, realmId, object : DiscussionsSyncRealmContainer.DiscussionsCallback {
            override fun handleDiscussions(discussions: List<DiscussionQuestionDto>) {
                if (discussions.isEmpty()) {
                    commentsView?.showError()
                } else {
                    val discussion = discussions[0]
                    commentsView?.showComments(discussion, account)
                }
            }
        })
    }

    override fun handleAuthResult(requestCode: Int, resultCode: Int, data: Intent) = authContainer.onAuthResult(requestCode, resultCode, data)

    override fun unBind() {
        activityView = null
        authContainer.removeAccountCallback()
        authContainer.unbindDiscussionActivityView()
    }

    override fun unbindDiscussionsView() {
        discussionsView = null
    }

    override fun unbindDiscussionsCommentsView() {
        commentsView = null
    }

    override fun unbindAddDiscussionsView() {
        addDiscussionsView = null
    }
}