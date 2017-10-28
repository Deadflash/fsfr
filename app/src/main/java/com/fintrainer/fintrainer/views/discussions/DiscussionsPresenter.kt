package com.fintrainer.fintrainer.views.discussions

import android.content.Intent
import com.fintrainer.fintrainer.di.contracts.DiscussionsContract
import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.utils.Constants.REALM_FAIL_CONNECT_CODE
import com.fintrainer.fintrainer.utils.Constants.REALM_SUCCESS_CONNECT_CODE
import com.fintrainer.fintrainer.utils.GoogleAuthContainer
import com.fintrainer.fintrainer.utils.RealmContainer
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentAddDiscussionView
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentComments
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentDiscussions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Created by krotk on 28.10.2017.
 */
class DiscussionsPresenter(private val realmContainer: RealmContainer,
                           private val authContainer: GoogleAuthContainer)
    : DiscussionsContract.Presenter, RealmContainer.DiscussionRealmCallBack {

    private var activityView: DiscussionsContract.View? = null
    private var discussionsView: DiscussionsContract.DiscussionsView? = null
    private var commentsView: DiscussionsContract.CommentsView? = null
    private var addDiscussionsView: DiscussionsContract.AddDiscussionView? = null

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
        addDiscussionsView = iView as? FragmentAddDiscussionView
    }

    override fun initDiscussionsRealm() {
        authContainer.getAccount(object : GoogleAuthContainer.AccountCallback {
            override fun onError(code: Int) {
                activityView?.realmStatus(REALM_FAIL_CONNECT_CODE)
            }

            override fun onSuccess(account: GoogleSignInAccount?) {
                account?.let { realmContainer.initDiscussionsRealm(it, this@DiscussionsPresenter) }
            }
        })
    }

    override fun realmConfigCallback(code: Int) {
        activityView?.realmStatus(REALM_SUCCESS_CONNECT_CODE)
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