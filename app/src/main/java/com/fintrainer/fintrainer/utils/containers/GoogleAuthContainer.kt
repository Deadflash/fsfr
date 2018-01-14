package com.fintrainer.fintrainer.utils.containers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.di.contracts.AuthContract
import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.utils.Constants.AUTH_INTERNET_CONNECTION_ERROR
import com.fintrainer.fintrainer.utils.Constants.RC_SIGN_IN
import com.fintrainer.fintrainer.views.discussions.DiscussionsActivity
import com.fintrainer.fintrainer.views.drawer.DrawerActivity
import com.fintrainer.fintrainer.views.testing.TestingActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * Created by krotk on 26.10.2017.
 */

class GoogleAuthContainer : GoogleApiClient.OnConnectionFailedListener, AuthContract.AuthContainer {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var view: AuthContract.View? = null
    private var account: GoogleSignInAccount? = null
    private var discussionActivity: DiscussionsActivity? = null
    private var accountCallBack: AccountCallback? = null

    override fun bind(iView: IView) {
        if (iView is DrawerActivity) {
            view = iView
        }
    }

    override fun bindDiscussionActivityView(discussionActivity: DiscussionsActivity) {
        this.discussionActivity = discussionActivity
    }

    override fun unbindDiscussionActivityView() {
        discussionActivity = null
    }

//    override fun bindTestingActivityView(testingActivity: TestingActivity) {
//        this.testingActivity = testingActivity
//    }
//
//    override fun unbindTestingActivityView() {
//        this.testingActivity = null
//    }

    override fun getAccount(accountCallback: AccountCallback) {
        this.accountCallBack = accountCallback
        if (account != null) {
            accountCallBack?.onSuccess(account)
        } else {
            login()
        }
    }

    override fun isAuthenticated() : Boolean = account != null

    override fun removeAccountCallback() {
        accountCallBack = null
    }

    override fun login() {
        doAsync {
            checkApiClientStatus()
            Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).setResultCallback { googleSignInResult ->
                uiThread {
                    handleSignInResult(googleSignInResult)
                }
            }
        }
    }

    override fun logout() {
        doAsync {
            checkApiClientStatus()
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback { callback ->
                uiThread {
                    if (callback.isSuccess) {
                        println("logout: ${callback.statusCode} CODE: ${callback.status} ${callback.status.statusCode}")
                        account = null
                        (view as? DrawerActivity)?.getString(R.string.my_exams)?.let { it1 -> showUserInfo(it1, Uri.EMPTY, false) }
                        view?.setLoginLogoutButtonsClickable()
                    } else {
                        view?.setLoginLogoutButtonsClickable()
                    }
                }
            }
        }
    }

    override fun tryToLogin() {
        if (account == null) {
            doAsync {
                checkApiClientStatus()
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).setResultCallback { googleSignInResult ->
                    uiThread {
                        if (googleSignInResult.isSuccess) {
                            account = googleSignInResult.signInAccount
                            showUserInfo(account?.displayName ?: "", account?.photoUrl ?: Uri.EMPTY, true)
                        } else if (googleSignInResult.status.statusCode == AUTH_INTERNET_CONNECTION_ERROR) {
                            showInternetConnectionError()
                        }
                    }
                }
            }
        } else {
            showUserInfo(account?.displayName ?: "", account?.photoUrl ?: Uri.EMPTY, true)
        }
    }

    private fun showUserInfo(userName: String, userAvatarUri: Uri, isLoggedIn: Boolean) {
        view?.showUserInfo(userName, userAvatarUri, isLoggedIn)
    }

    private fun checkApiClientStatus() {
        if (mGoogleApiClient == null) {
            setupGoogleApiClient()
        }
        if (!mGoogleApiClient?.isConnected!!) {
            mGoogleApiClient!!.blockingConnect()
        }
    }

    private fun setupGoogleApiClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken((view as? DrawerActivity)?.getString(R.string.server_client_id))
                .build()

        mGoogleApiClient = (view as? DrawerActivity)?.let {
            GoogleApiClient.Builder(it)
                    .enableAutoManage(view as DrawerActivity, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build()
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            account = result.signInAccount
            accountCallBack?.onSuccess(result.signInAccount!!)
            showUserInfo(account?.displayName!!, account?.photoUrl!!, true)
            view?.refreshStatistics()
        } else {
            if (result.status.statusCode == AUTH_INTERNET_CONNECTION_ERROR) {
                showInternetConnectionError()
            } else {
                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                (view as? DrawerActivity)?.startActivityForResult(signInIntent, RC_SIGN_IN)
                discussionActivity?.startActivityForResult(signInIntent, RC_SIGN_IN)
//                testingActivity?.showNeedAuth()
            }
        }
    }

    private fun showInternetConnectionError() {
        (view as? DrawerActivity)?.toast(R.string.internet_connection_error)
        view?.setLoginLogoutButtonsClickable()
        discussionActivity?.toast(R.string.internet_connection_error)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (!connectionResult.hasResolution()) {
            println("Connection failed")
        }
    }

    override fun onAuthResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data))
        } else {
            accountCallBack?.onError(resultCode)
            view?.setLoginLogoutButtonsClickable()
        }
    }

    override fun unBind() {
        view = null
    }

    interface AccountCallback {
        fun onSuccess(account: GoogleSignInAccount?)
        fun onError(code: Int)
    }
}