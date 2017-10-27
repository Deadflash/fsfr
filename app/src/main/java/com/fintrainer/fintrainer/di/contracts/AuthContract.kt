package com.fintrainer.fintrainer.di.contracts

import android.net.Uri

/**
 * Created by krotk on 27.10.2017.
 */
interface AuthContract {
    interface View : IView {
        fun showUserInfo(userName: String, userAvatarUri: Uri, isLoggedIn: Boolean)
        fun setClickable()
    }

    interface AuthContainer : IPresenter {
        fun login()
        fun logout()
        fun tryToLogin()
    }
}