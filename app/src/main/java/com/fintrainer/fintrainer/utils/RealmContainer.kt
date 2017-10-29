package com.fintrainer.fintrainer.utils

import com.fintrainer.fintrainer.structure.*
import com.fintrainer.fintrainer.utils.Constants.EXAM_INTENT
import com.fintrainer.fintrainer.utils.Constants.RANDOM_TESTS_COUNT
import com.fintrainer.fintrainer.utils.Constants.REALM_FAIL_CONNECT_CODE
import com.fintrainer.fintrainer.utils.Constants.REALM_SERVER_AUTH
import com.fintrainer.fintrainer.utils.Constants.REALM_SERVER_DISCUSSION_REALM
import com.fintrainer.fintrainer.utils.Constants.REALM_SERVER_SCHEMA_VERSION
import com.fintrainer.fintrainer.utils.Constants.REALM_SERVER_URL
import com.fintrainer.fintrainer.utils.Constants.REALM_SUCCESS_CONNECT_CODE
import com.fintrainer.fintrainer.utils.Constants.TESTING_INTENT
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.realm.*
import io.realm.annotations.RealmModule
import java.util.*

/**
 * Created by krotk on 23.10.2017.
 */
class RealmContainer {

    private val fk = "�e�\\u001Dj�4\\u0016P\\u0003h:���aa"
    private val fk2 = "{b����\\u0002"

    private lateinit var statisticConf: RealmConfiguration
    private var discussionsConfig: RealmConfiguration? = null
    private var discussionsRealm: Realm? = null

    fun initRealm() {
        val config = RealmConfiguration.Builder()
                .name("n")
                .assetFile("n") // e.g "default.realmWithExams" or "lib/data.realmWithExams"
                .modules(Default())
                .encryptionKey((fk + fk2).toByteArray())
                .schemaVersion(1)
                .build()

//        Realm.compactRealm(config);
        Realm.setDefaultConfiguration(config)
    }

    fun initStatisticsRealm() {
        statisticConf = RealmConfiguration.Builder()
                .name("q")
                .modules(Statistics())
                .schemaVersion(2)
                .migration(Migration())
                .build()
    }

//    fun createRealm() {
//        val myCredentials = SyncCredentials.usernamePassword("fcpunlimited@gmail.com", " 3535583q", false)
//        SyncUser.loginAsync(myCredentials, "http://91.240.84.213:9080/auth", object : SyncUser.RequestCallback<SyncUser>, SyncUser.Callback {
//            override fun onSuccess(user: SyncUser) {
//
//                val config = SyncConfiguration.Builder(user, "realm://91.240.84.213:9080" + REALM_SERVER_DISCUSSION_REALM)
//                        .schemaVersion(REALM_SERVER_SCHEMA_VERSION)
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
//                        val request = PermissionRequest(condition, "realm://91.240.84.213:9080" + REALM_SERVER_DISCUSSION_REALM, accessLevel)
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
            SyncUser.loginAsync(SyncCredentials.google(account.idToken), REALM_SERVER_URL + REALM_SERVER_AUTH, object : SyncUser.RequestCallback<SyncUser>, SyncUser.Callback {
                override fun onSuccess(result: SyncUser?) {
                    discussionsConfig = SyncConfiguration.Builder(result, REALM_SERVER_URL + REALM_SERVER_DISCUSSION_REALM)
                            .schemaVersion(REALM_SERVER_SCHEMA_VERSION)
                            .waitForInitialRemoteData()
                            .build()
                    realmCallBack.realmConfigCallback(REALM_SUCCESS_CONNECT_CODE)
                }

                override fun onError(error: ObjectServerError?) {
                    realmCallBack.realmConfigCallback(error?.errorCode?.intValue() ?: REALM_FAIL_CONNECT_CODE)
                }
            })
        } else {
            realmCallBack.realmConfigCallback(REALM_SUCCESS_CONNECT_CODE)
        }
    }

    private fun getDiscussionRealm(): Realm{
        if (discussionsRealm == null){
            discussionsRealm = Realm.getInstance(discussionsConfig)
        }
        return discussionsRealm!!
    }

    fun testRealm(){
//        val testRealm = getDiscussionRealm()
        getDiscussionRealm().beginTransaction()
        val discussion = DiscussionQuestionDto()
        discussion.id = UUID.randomUUID().toString()
        discussion.questionId = "1.1.1"
        discussion.questionType = 0
        discussion.text = "hardcodetest"
        discussion.discussionCreator = "TEST"
        getDiscussionRealm().commitTransaction()
    }

    fun closeTestRealm(){
        if (discussionsRealm != null){
            discussionsRealm!!.close()
        }
    }

    fun getExamInformation(examId: Int): ExamStatisticAndInfo {
        Realm.getDefaultInstance().use {
            val chapterCount: Int? = it.where(ChapterRealm::class.java).equalTo("type", examId).count().toInt()
            val questionsCount: Int? = it.where(TestingDto::class.java).equalTo("type", examId).count().toInt()
            var favouriteQuestionsCount: Int? = 0
            Realm.getInstance(statisticConf).use {
                favouriteQuestionsCount = it.where(FavouriteQuestionsDto::class.java).equalTo("type", examId).count().toInt()
            }
            return ExamStatisticAndInfo(getAverageGrade(examId), getAverageRightAnswersCount(examId), chapterCount ?: 0, questionsCount ?: 0, favouriteQuestionsCount ?: 0)

        }
    }

    fun createDiscussion(user: String, text: String, questionCode: String, questionType: Int, createDiscussionCallback: CreateDiscussionCallback) {
        if (discussionsConfig != null) {
            Realm.getInstanceAsync(discussionsConfig, object : Realm.Callback() {
                override fun onSuccess(realm: Realm?) {
                    realm.use {
                        val discussion = DiscussionQuestionDto()
                        discussion.id = UUID.randomUUID().toString()
                        discussion.questionId = questionCode
                        discussion.questionType = questionType
                        discussion.text = text
                        discussion.discussionCreator = user
                        it?.executeTransaction({
                            realm ->
                            realm.copyToRealm(discussion)
                            createDiscussionCallback.success()
                        })
                    }
                }

                override fun onError(exception: Throwable?) {
                    createDiscussionCallback.error(REALM_FAIL_CONNECT_CODE)
                }

            })
        }
    }

    fun getDiscussions(questionCode: String, questionType: Int, discussionsCallback: DiscussionsCallback) {
        if (discussionsConfig != null) {
            Realm.getInstanceAsync(discussionsConfig, object : Realm.Callback() {
                override fun onSuccess(realm: Realm?) {
                    realm.use {
                        it?.where(DiscussionQuestionDto::class.java)
                                ?.equalTo("questionId", questionCode)
                                ?.equalTo("questionType", questionType)
                                ?.findAll()
                                ?.let { it1 -> discussionsCallback.handleDiscussions(it1) }
                    }
                }

                override fun onError(exception: Throwable?) {
                    println("Realm Error ${exception?.message} ${exception?.cause}")
                }
            })
        } else {
            println("Discussions config == null")
        }
    }

    private fun getAverageGrade(examId: Int): Int {
        Realm.getInstance(statisticConf).use {
            val averageGrade = it.where(AverageGradeStatisticDto::class.java)
                    .equalTo("examType", EXAM_INTENT).equalTo("testType", examId).findAll()
            var average = 0
            if (!averageGrade.isEmpty()) {
                val gradesCount = averageGrade.size
                val gradesSum = averageGrade.sumBy { it.grade!! }
                average = gradesSum / gradesCount
            }
            return average
        }
    }

    private fun getAverageRightAnswersCount(examId: Int): Int {
        Realm.getInstance(statisticConf).use {
            val averageRightAnswers = it.where(AverageGradeStatisticDto::class.java)
                    .equalTo("examType", TESTING_INTENT).equalTo("testType", examId).findAll()
            var average = 0
            if (!averageRightAnswers.isEmpty()) {
                val gradesCount = averageRightAnswers.size
                val answersSum = averageRightAnswers.sumBy { it.rightAnswersCount!! }
                average = answersSum / gradesCount
            }
            return average
        }
    }

    fun getExamAsync(examId: Int): List<TestingDto> {
        val tests = randomExams(true, examId)
        shuffleAnswers(tests)
        return tests
    }

    private fun randomExams(needWeight: Boolean, examId: Int): List<TestingDto> {
        Realm.getDefaultInstance().use {
            val testingDtos = ArrayList<TestingDto>()
            val count = it.where(TestingDto::class.java).equalTo("type", examId).count()
            if (count == 0L) {
                return emptyList()
            }
            val list = (1 until count).toList()
            var iterationCount = 100
            if (list.size < 100) {
                iterationCount = list.size
            }
            Collections.shuffle(list)
            var i = 0
            while (i < iterationCount) {
                val testingDto = it.where(TestingDto::class.java).equalTo("type", examId).equalTo("index", list[i]).findFirst()
                if (testingDto != null) {
                    testingDtos.add(it.copyFromRealm(testingDto))
                    i = if (needWeight) testingDto.weight!! + i else i + 1
                }
            }
            return testingDtos
        }
    }

    fun getTestsAsync(examId: Int): List<TestingDto> {
        val testingDtos = ArrayList<TestingDto>()
        Realm.getDefaultInstance().use {
            //            if (!purchased) {
            if (!true) {
                val chaptersCount = it.where(ChapterRealm::class.java).equalTo("type", examId).count()
                (0L until chaptersCount)
                        .map { i -> it.where(TestingDto::class.java).equalTo("type", examId).equalTo("chapter", i + 1).findAll() }
                        .forEach { freeTests -> (0..4).mapTo(testingDtos) { o -> it.copyFromRealm(freeTests[o]) } }
                return testingDtos
            } else {
                return randomPurchasedTests(it, RANDOM_TESTS_COUNT, false, examId)
            }
        }
    }

    private fun randomPurchasedTests(realmWithExams: Realm, iterationCount: Int, needWeight: Boolean, examId: Int): List<TestingDto> {
        val testingDtos = ArrayList<TestingDto>()
        var iterationCount = iterationCount
        val count = realmWithExams.where(TestingDto::class.java).equalTo("type", examId).count()
        if (count == 0L) {
//            Snackbar.make(activity.findViewById(R.id.fragmentContainer), "Ошибка", BaseTransientBottomBar.LENGTH_SHORT).show()
            return emptyList()
        }
        val list = (1 until count).toList()
        if (list.size < 100) {
            iterationCount = list.size
        }
        Collections.shuffle(list)
        var i = 0
        while (i < iterationCount) {
            val testingDto = realmWithExams.where(TestingDto::class.java).equalTo("type", examId).equalTo("index", list[i]).findFirst()
            if (testingDto != null) {
                testingDtos.add(realmWithExams.copyFromRealm(testingDto))
                i = if (needWeight) testingDto.weight!! + i else i + 1
            }
        }
        shuffleAnswers(testingDtos)
        return testingDtos
    }

    fun getChaptersAsync(examId: Int): List<ChapterRealm> {
        Realm.getDefaultInstance().use {
            return it.copyFromRealm(it.where(ChapterRealm::class.java).equalTo("type", examId).findAll())

        }
    }

    fun getByChapterAsync(chapter: Int, examId: Int): List<TestingDto> {
        var testingDtos: MutableList<TestingDto> = ArrayList()
        Realm.getDefaultInstance().use {
            val freeTests = it.where(TestingDto::class.java).equalTo("type", examId).equalTo("chapter", chapter).findAll()
            if (!freeTests.isEmpty()) {
//                    if (!purchased) {
                if (false) {
                    (0..4).mapTo(testingDtos) { i -> it.copyFromRealm<TestingDto>(freeTests[i]) }
                } else {
                    testingDtos = it.copyFromRealm<TestingDto>(freeTests)
                }
                shuffleAnswers(testingDtos)
                return testingDtos
            }
        }
        return testingDtos
    }

    fun getFavouriteQuestionsAsync(examId: Int): List<TestingDto> {
        Realm.getInstance(statisticConf).use {
            var questions = mutableListOf<TestingDto>()
            val favouriteQuestions = it.where(FavouriteQuestionsDto::class.java).equalTo("type", examId).findAll()
            if (!favouriteQuestions.isEmpty()) {
                Realm.getDefaultInstance().use {
                    favouriteQuestions
                            .mapNotNull { questionsDto ->
                                it.where(TestingDto::class.java)
                                        .equalTo("type", questionsDto.type)
                                        .equalTo("index", questionsDto.index)
                                        .findFirst()
                            }
                            .mapTo(questions) { testingDto -> it.copyFromRealm(testingDto) as TestingDto }
                }
            }
            return questions
        }
    }

    private fun shuffleAnswers(testingDtos: List<TestingDto>?) {
//        if (sharedPreferences.getBoolean("key_shuffle_answers", false)) {
//            for (testingDto in testingDtos) {
//                Collections.shuffle(testingDto.answers!!)
//            }
//        }
    }

    fun getAllQuestionsAsync(examId: Int): List<TestingDto> {
        var questions: List<TestingDto> = emptyList()
        Realm.getDefaultInstance().use {
            questions = it.copyFromRealm(it.where(TestingDto::class.java).equalTo("type", examId).findAll())
        }
        return questions
    }

    fun saveResults(intentId: Int, weight: Int, testType: Int, rightAnswers: Int) {
        Realm.getInstance(statisticConf).use {
            it.beginTransaction()
            val statistics = AverageGradeStatisticDto()
            statistics.examType = intentId
            statistics.rightAnswersCount = rightAnswers
            statistics.testType = testType
            statistics.grade = if (weight > 100) 100 else weight
            it.copyToRealm(statistics)
            it.commitTransaction()
        }
    }

    fun checkQuestionIsFavouriteAsync(index: Int, type: Int): Boolean {
        Realm.getInstance(statisticConf).use {
            return !it.where(FavouriteQuestionsDto::class.java)
                    .equalTo("index", index).equalTo("type", type).findAll().isEmpty()
        }
    }

    fun addRemoveFavourite(isAdding: Boolean, index: Int, type: Int, chapter: Int) {
        Realm.getInstance(statisticConf).use {
            if (isAdding) {
                val favQuestion = FavouriteQuestionsDto()
                favQuestion.index = index.toLong()
                favQuestion.type = type
                favQuestion.chapter = chapter
                it.executeTransaction({ it.copyToRealm(favQuestion) })
            } else {
                it.executeTransaction({ realm ->
                    val questionToRemove = realm.where(FavouriteQuestionsDto::class.java)
                            .equalTo("index", index)
                            .equalTo("type", type)
                            .findAll()
                    if (!questionToRemove.isEmpty()) {
                        questionToRemove.deleteAllFromRealm()
                    }
                })
            }
        }
    }

    interface DiscussionRealmCallBack {
        fun realmConfigCallback(code: Int)
    }

    interface DiscussionsCallback {
        fun handleDiscussions(discussions: List<DiscussionQuestionDto>)
    }

    interface CreateDiscussionCallback {
        fun success()
        fun error(code: Int)
    }

    @RealmModule(classes = arrayOf(ExamDto::class, TestingDto::class, ChapterRealm::class, AnswersDto::class))
    private inner class Default

    @RealmModule(classes = arrayOf(CorrectedTestDto::class, AverageGradeStatisticDto::class, FavouriteQuestionsDto::class))
    inner class Statistics
}