package com.fintrainer.fintrainer.utils.containers

import com.fintrainer.fintrainer.structure.DiscussionCommentDto
import com.fintrainer.fintrainer.structure.DiscussionQuestionDto
import com.fintrainer.fintrainer.structure.DiscussionRateDto
import com.fintrainer.fintrainer.structure.TestingDto
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

//    fun createRealm() {
//        val myCredentials = SyncCredentials.usernamePassword("fcpunlimited@gmail.com", " 3535583q", false)
//        SyncUser.loginAsync(myCredentials, "http://91.240.84.213:9080/auth", object : SyncUser.RequestCallback<SyncUser>, SyncUser.Callback {
//            override fun onSuccess(user: SyncUser) {
//
//                val config = SyncConfiguration.Builder(user, "realm://91.240.84.213:9080" + Constants.REALM_SERVER_DISCUSSION_REALM)
//                        .schemaVersion(Constants.REALM_SERVER_SCHEMA_VERSION)
//                        .waitForInitialRemoteData()
//
//                        //.waitForRemoteInitialData()
//                        .build()
//                //               Realm.deleteRealm(config);
//                Realm.getInstanceAsync(config, object : Realm.Callback() {
//                    override fun onSuccess(incRealm: Realm) {
////                        realm = incRealm
//                        val pm = user.permissionManager
//                        val condition = UserCondition.noExistingPermissions()
//                        val accessLevel = AccessLevel.WRITE
//                        val request = PermissionRequest(condition, "realm://91.240.84.213:9080" + Constants.REALM_SERVER_DISCUSSION_REALM, accessLevel)
//
//                        pm.applyPermissions(request, object : PermissionManager.ApplyPermissionsCallback {
//                            override fun onSuccess() {
//                                println("Hooray")
//                                // throw new RuntimeException("Horray");
//                            }
//
//                            override fun onError(error: ObjectServerError) {
//                                throw RuntimeException(error)
//                            }
//                        })
//                    }
//                })
//            }
//
//            override fun onError(error: ObjectServerError) {
//                throw RuntimeException(error)
//            }
//        })
//
//    }

    fun initDiscussionsRealm(account: GoogleSignInAccount, realmCallBack: DiscussionRealmCallBack) {
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
            discussionsConfig?.let {
                Realm.getInstanceAsync(it, object : Realm.Callback() {
                    override fun onSuccess(realm: Realm?) {
                        discussionsRealm = realm
                        realmInstanceCallback.successCallback()
                    }

                    override fun onError(exception: Throwable?) {
                        realmInstanceCallback.errorCallBack()
                    }
                })
            }
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

    fun createDiscussion(user: String, text: String, questionCode: String, questionType: Int, realmCallback: RealmCallback) {
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
                realmCallback.success()
            }

            override fun errorCallBack() {

            }
        })
    }

    fun getDiscussions(questionId: String, questionType: Int, discussionsCallback: DiscussionsCallback) {
        getDiscussionsRealmInstance(object : RealmInstanceCallback {

            override fun successCallback() {

                val discussions = discussionsRealm?.where(DiscussionQuestionDto::class.java)
                        ?.equalTo("questionId", questionId)
                        ?.equalTo("questionType", questionType)
                        ?.findAll()?.toList() ?: emptyList()

                if (!discussions.isEmpty()) {
                    discussionsRealm?.beginTransaction()
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
                    discussionsRealm?.commitTransaction()
                }
                discussionsCallback.handleDiscussions(discussions)
            }

            override fun errorCallBack() {
                discussionsCallback.handleDiscussions(emptyList())
            }
        })
    }

    fun getApprovedHints(tests: List<TestingDto>, callback: HintsCallback) {
        getDiscussionsRealmInstance(object : RealmInstanceCallback {
            override fun successCallback() {
                val hints = mutableListOf<DiscussionCommentDto>()
                tests.forEach {
                    val question = discussionsRealm?.where(DiscussionQuestionDto::class.java)?.equalTo("questionType", it.type)?.equalTo("questionId", it.code)?.findAll()
                    question?.let {
                        if (!it.isEmpty() && !it[0].commentList?.isEmpty()!!) {
                            val questionDto = discussionsRealm?.copyFromRealm(it)
                            questionDto?.let { it[0]?.commentList?.let { hints.add(it[0]) } }
                        } else {
                            val comment = DiscussionCommentDto()
                            comment.text = null
                            hints.add(comment)
                        }
                    }
                }
                callback.showHints(hints)
            }

            override fun errorCallBack() {
                callback.showHints(emptyList())
            }

        })
    }

    fun rateDiscussion(discussion: DiscussionQuestionDto, account: GoogleSignInAccount, rate: Boolean, realmCallback: RealmCallback) {
        getDiscussionsRealmInstance(object : RealmInstanceCallback {
            override fun successCallback() {
                val result = discussion.rateList!!.where().equalTo("userId", account.email).findAll()
                result?.let {
                    if (it.size == 0) {
                        discussionsRealm?.beginTransaction()
                        val rateResult = discussionsRealm?.createObject(DiscussionRateDto::class.java, UUID.randomUUID().toString())
                        rateResult?.parent = discussion
                        rateResult?.userId = account.email
                        rateResult?.direction = rate
                        discussion.rateList?.add(rateResult)
                        discussionsRealm?.commitTransaction()

                        realmCallback.success()
                    } else {
                        val discussionRate = result[0]
                        discussionsRealm?.beginTransaction()
                        discussionRate.direction?.let {
                            if (it == rate) {
                                discussionRate.deleteFromRealm()
                            } else {
                                discussionRate.direction = rate
                            }
                        }
                        discussionsRealm?.commitTransaction()
                        realmCallback.success()
                    }
                }
            }

            override fun errorCallBack() {

            }

        })
    }

    fun rateComment(comment: DiscussionCommentDto, account: GoogleSignInAccount, rate: Boolean, realmCallback: RealmCallback) {
        getDiscussionsRealmInstance(object : RealmInstanceCallback {
            override fun successCallback() {
                val result = comment.rateList?.where()?.equalTo("userId", account.email)?.findAll()
                result?.let {
                    if (it.size == 0) {
                        discussionsRealm?.beginTransaction()
                        val rateResult = discussionsRealm?.createObject(DiscussionRateDto::class.java, UUID.randomUUID().toString())
                        rateResult?.parent = comment.parent
                        rateResult?.userId = account.email
                        rateResult?.direction = rate
                        comment.rateList?.add(rateResult)
                        discussionsRealm?.commitTransaction()
                        realmCallback.success()
                    } else {
                        val commentRate = result[0]
                        discussionsRealm?.beginTransaction()
                        commentRate.direction?.let {
                            if (it == rate) {
                                commentRate.deleteFromRealm()
                            } else {
                                commentRate.direction = rate
                            }
                        }
                        discussionsRealm?.commitTransaction()
                        realmCallback.success()
                    }
                }
            }

            override fun errorCallBack() {

            }
        })
    }

    fun addComment(discussion: DiscussionQuestionDto, commentMessage: String, account: GoogleSignInAccount, realmCallBack: RealmCallback) {
        getDiscussionsRealmInstance(object : RealmInstanceCallback {
            override fun successCallback() {
                discussionsRealm?.beginTransaction()
                val comment = discussionsRealm?.createObject(DiscussionCommentDto::class.java, UUID.randomUUID().toString())
                comment?.text = commentMessage
                comment?.parent = discussion
                comment?.commentCreator = account.displayName
                discussion.commentList?.add(comment)
                discussionsRealm?.commitTransaction()
                realmCallBack.success()
            }

            override fun errorCallBack() {
                realmCallBack.error(-1)
            }
        })
    }

    fun getDiscussionComments(questionId: String, questionType: Int, realmId: String, commentsCallback: DiscussionsCallback) {
        getDiscussionsRealmInstance(object : RealmInstanceCallback {
            override fun successCallback() {

                val discussions = discussionsRealm?.where(DiscussionQuestionDto::class.java)
                        ?.equalTo("questionId", questionId)
                        ?.equalTo("questionType", questionType)
                        ?.equalTo("id", realmId)
                        ?.findAll()?.toList() ?: emptyList()

                if (!discussions.isEmpty()) {
                    discussionsRealm?.beginTransaction()
                    discussions[0].commentList?.sortedWith(Comparator { t1, t2 ->
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
                    discussionsRealm?.commitTransaction()
                }
                commentsCallback.handleDiscussions(discussions)
            }

            override fun errorCallBack() {
                commentsCallback.handleDiscussions(emptyList())
            }
        })
    }

    interface DiscussionRealmCallBack {
        fun realmConfigCallback(code: Int)
    }

    interface HintsCallback {
        fun showHints(hints: List<DiscussionCommentDto>)
    }

    interface DiscussionsCallback {
        fun handleDiscussions(discussions: List<DiscussionQuestionDto>)
    }

    interface RealmCallback {
        fun success()
        fun error(code: Int)
    }

    interface RealmInstanceCallback {
        fun successCallback()
        fun errorCallBack()
    }
}