package com.fintrainer.fintrainer.di.contracts

import android.content.Intent
import android.net.Uri
import com.fintrainer.fintrainer.utils.containers.GoogleAuthContainer
import com.fintrainer.fintrainer.views.discussions.DiscussionsActivity
import com.fintrainer.fintrainer.views.testing.TestingActivity

/**
 * Created by krotk on 27.10.2017.
 */
interface AuthContract {
    interface View : IView {
        fun showUserInfo(userName: String, userAvatarUri: Uri, isLoggedIn: Boolean)
        fun refreshStatistics()
        fun setLoginLogoutButtonsClickable()
    }

    interface AuthContainer : IPresenter {

        fun login()
        fun logout()
        fun isAuthenticated(): Boolean
        fun tryToLogin()
        fun onAuthResult(requestCode: Int, resultCode: Int, data: Intent)
        fun getAccount(accountCallback: GoogleAuthContainer.AccountCallback)
        fun removeAccountCallback()
        fun bindDiscussionActivityView(discussionActivity: DiscussionsActivity)
        fun unbindDiscussionActivityView()
//        fun bindTestingActivityView(testingActivity: TestingActivity)
//        fun unbindTestingActivityView()
    }
}