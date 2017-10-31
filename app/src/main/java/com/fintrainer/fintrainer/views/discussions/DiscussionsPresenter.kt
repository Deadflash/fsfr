package com.fintrainer.fintrainer.views.discussions

import android.content.Intent
import com.fintrainer.fintrainer.di.contracts.DiscussionsContract
import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.structure.DiscussionQuestionDto
import com.fintrainer.fintrainer.utils.Constants.REALM_ERROR_CODE
import com.fintrainer.fintrainer.utils.Constants.REALM_SUCCES_CODE
import com.fintrainer.fintrainer.utils.containers.DiscussionsSyncRealmContainer
import com.fintrainer.fintrainer.utils.containers.GoogleAuthContainer
import com.fintrainer.fintrainer.utils.containers.RealmContainer
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentAddDiscussion
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentComments
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentDiscussions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Created by krotk on 28.10.2017.
 */
class DiscussionsPresenter(private val discussionsSyncRealmContainer: DiscussionsSyncRealmContainer,
                           private val authContainer: GoogleAuthContainer)
    : DiscussionsContract.Presenter, RealmContainer.DiscussionRealmCallBack {

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
//        realmContainer.testRealm()
    }

    override fun initDiscussionsRealm() {
        authContainer.getAccount(object : GoogleAuthContainer.AccountCallback {
            override fun onError(code: Int) {
                activityView?.realmStatus(code)
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
        activityView?.realmStatus(code)
    }

    override fun onCreateDiscussionClicked() {
        addDiscussionsView?.onCreateDiscussionClicked()
    }

    override fun createDiscussion(text: String, questionCode: String, questionType: Int) {
        if (account != null) {
            account!!.displayName?.let {
                discussionsSyncRealmContainer.createDiscussion(it, text, questionCode, questionType, object : RealmContainer.CreateDiscussionCallback {
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

    override fun getDiscussions(code: String, questionType: Int) {
        discussionsSyncRealmContainer.getDiscussions(code, questionType, object : RealmContainer.DiscussionsCallback {
            override fun handleDiscussions(discussions: List<DiscussionQuestionDto>) {

                discussions.sortedWith(Comparator { t1, t2 ->
                    var o1Rate = 0
                    var o2Rate = 0

                    t1.rateList?.let {
                        it.forEach { rateElem ->
                            rateElem.direction?.let {
                                o1Rate = if (it) o1Rate.plus(1) else o1Rate.minus(1)
                            }
                        }
                    }
                    t2.rateList?.let {
                        it.forEach { rateElem ->
                            rateElem.direction?.let {
                                o2Rate = if (it) o2Rate.plus(1) else o2Rate.minus(1)
                            }
                        }
                    }

                    return@Comparator o2Rate - o1Rate
                })
                discussionsView?.showDiscussions(discussions, account)
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
//        realmContainer.closeDiscussionRealm()
    }
}