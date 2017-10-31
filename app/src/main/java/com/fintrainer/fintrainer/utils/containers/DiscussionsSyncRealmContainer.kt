package com.fintrainer.fintrainer.utils.containers

import com.fintrainer.fintrainer.structure.DiscussionQuestionDto
import com.fintrainer.fintrainer.utils.Constants
import com.fintrainer.fintrainer.utils.Constants.REALM_FAIL_CONNECT_CODE
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.realm.*
import io.realm.permissions.AccessLevel
import io.realm.permissions.PermissionRequest
import io.realm.permissions.UserCondition
import java.util.*

/**
 * Created by deadf on 31.10.2017.
 */
class DiscussionsSyncRealmContainer {

    private var discussionsConfig: RealmConfiguration? = null
    private var discussionsRealm: Realm? = null

    fun createRealm() {
        val myCredentials = SyncCredentials.usernamePassword("fcpunlimited@gmail.com", " 3535583q", false)
        SyncUser.loginAsync(myCredentials, "http://91.240.84.213:9080/auth", object : SyncUser.RequestCallback<SyncUser>, SyncUser.Callback {
            override fun onSuccess(user: SyncUser) {

                val config = SyncConfiguration.Builder(user, "realm://91.240.84.213:9080" + Constants.REALM_SERVER_DISCUSSION_REALM)
                        .schemaVersion(Constants.REALM_SERVER_SCHEMA_VERSION)
                        .waitForInitialRemoteData()

                        //.waitForRemoteInitialData()
                        .build()
                //               Realm.deleteRealm(config);
                Realm.getInstanceAsync(config, object : Realm.Callback() {
                    override fun onSuccess(incRealm: Realm) {
//                        realm = incRealm
                        val pm = user.permissionManager
                        val condition = UserCondition.noExistingPermissions()
                        val accessLevel = AccessLevel.WRITE
                        val request = PermissionRequest(condition, "realm://91.240.84.213:9080" + Constants.REALM_SERVER_DISCUSSION_REALM, accessLevel)

                        pm.applyPermissions(request, object : PermissionManager.ApplyPermissionsCallback {
                            override fun onSuccess() {
                                println("Hooray")
                                // throw new RuntimeException("Horray");
                            }

                            override fun onError(error: ObjectServerError) {
                                throw RuntimeException(error)
                            }
                        })
                    }
                })
            }

            override fun onError(error: ObjectServerError) {
                throw RuntimeException(error)
            }
        })

    }

    fun initDiscussionsRealm(account: GoogleSignInAccount, realmCallBack: RealmContainer.DiscussionRealmCallBack) {
        if (discussionsConfig == null) {
            SyncUser.loginAsync(SyncCredentials.google(account.idToken), Constants.REALM_SERVER_HTTP_URL + Constants.REALM_SERVER_AUTH, object : SyncUser.RequestCallback<SyncUser>, SyncUser.Callback {

                override fun onSuccess(result: SyncUser?) {
                    discussionsConfig = SyncConfiguration.Builder(result, Constants.REALM_SERVER__REALM_URL + Constants.REALM_SERVER_DISCUSSION_REALM)
                            .schemaVersion(Constants.REALM_SERVER_SCHEMA_VERSION)
                            .waitForInitialRemoteData()
                            .build()
                    realmCallBack.realmConfigCallback(Constants.REALM_SUCCESS_CONNECT_CODE)
                }

                override fun onError(error: ObjectServerError?) {
                    val errorConnectCode = if (error?.errorCode?.intValue() == 0) REALM_FAIL_CONNECT_CODE else error?.errorCode?.intValue()
                    realmCallBack.realmConfigCallback(errorConnectCode ?: REALM_FAIL_CONNECT_CODE)
                }
            })
        } else {
            realmCallBack.realmConfigCallback(Constants.REALM_SUCCESS_CONNECT_CODE)
        }
    }

    private fun getDiscussionsRealmInstance(realmInstanceCallback: RealmInstanceCallback) {
        if (discussionsRealm == null) {
            Realm.getInstanceAsync(discussionsConfig, object : Realm.Callback() {
                override fun onSuccess(realm: Realm?) {
                    discussionsRealm = realm
                    realmInstanceCallback.successCallback()
                }

                override fun onError(exception: Throwable?) {
                    realmInstanceCallback.errorCallBack()
                }
            })
        } else {
            realmInstanceCallback.successCallback()
        }
    }

    fun closeDiscussionRealm() {
        if (discussionsRealm != null) {
            discussionsRealm!!.close()
            SyncUser.currentUser()?.logout()
            discussionsRealm = null
        }
    }

    fun createDiscussion(user: String, text: String, questionCode: String, questionType: Int, createDiscussionCallback: RealmContainer.CreateDiscussionCallback) {
        getDiscussionsRealmInstance(object : RealmInstanceCallback {
            override fun successCallback() {

                discussionsRealm?.beginTransaction()
                val disc = discussionsRealm?.createObject(DiscussionQuestionDto::class.java, UUID.randomUUID().toString())
                disc?.text = text
                disc?.questionId = questionCode
                disc?.questionType = questionType
                disc?.text = text
                disc?.discussionCreator = user
                discussionsRealm?.commitTransaction()
                createDiscussionCallback.success()
            }

            override fun errorCallBack() {

            }
        })
    }

    fun getDiscussions(questionCode: String, questionType: Int, discussionsCallback: RealmContainer.DiscussionsCallback) {
        getDiscussionsRealmInstance(object : RealmInstanceCallback {

            override fun successCallback() {
                discussionsCallback.handleDiscussions(discussionsRealm?.where(DiscussionQuestionDto::class.java)
                        ?.equalTo("questionId", questionCode)
                        ?.equalTo("questionType", questionType)
                        ?.findAll()?.toList() ?: emptyList())
            }

            override fun errorCallBack() {
                discussionsCallback.handleDiscussions(emptyList())
            }
        })
    }

    interface RealmInstanceCallback {
        fun successCallback()
        fun errorCallBack()
    }
}