package com.fintrainer.fintrainer.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.di.contracts.AuthContract
import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.utils.Constants.RC_SIGN_IN
import com.fintrainer.fintrainer.views.drawer.DrawerActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by krotk on 26.10.2017.
 */

class GoogleAuthContainer : GoogleApiClient.OnConnectionFailedListener, AuthContract.AuthContainer {

    private var activity: AppCompatActivity? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var view: AuthContract.View? = null

    override fun bind(iView: IView) {
        if (iView is DrawerActivity) {
            view = iView
            activity = iView
        }
    }

    override fun login() {
        doAsync {
            checkApiClientStatus()
            val opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
            uiThread {
                if (opr.isDone) {
                    handleSignInResult(opr.get())
                } else {
                    opr.setResultCallback { handleSignInResult(it) }
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
                        showUserInfo("ФСФР Экзамены", Uri.EMPTY, false)
                    }
                }
            }
        }
    }

    override fun tryToLogin() {
        doAsync {
            checkApiClientStatus()
            val opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
            uiThread {
                if (opr.isDone) {
                    val result = opr.get()
                    if (result.isSuccess) {
                        val account = result.signInAccount
                        showUserInfo(account?.displayName ?: "", account?.photoUrl ?: Uri.EMPTY, true)
                    }
                }
            }
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
                .requestIdToken(activity?.getString(R.string.server_client_id))
                .build()

        mGoogleApiClient = activity?.let {
            GoogleApiClient.Builder(it)
                    .enableAutoManage(activity!!, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build()
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account = result.signInAccount
            showUserInfo(account?.displayName!!, account?.photoUrl!!, true)
        } else {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            activity?.startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (!connectionResult.hasResolution()) {
            println("Connection failed")
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data))
        }else{
            view?.setClickable()
        }
    }

    override fun unBind() {
        view = null
        activity = null
    }
}