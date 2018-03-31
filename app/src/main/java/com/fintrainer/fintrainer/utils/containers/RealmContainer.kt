package com.fintrainer.fintrainer.utils.containers

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.SparseArray
import com.fintrainer.fintrainer.structure.*
import com.fintrainer.fintrainer.structure.chapters.ChapterStatistics
import com.fintrainer.fintrainer.utils.Constants
import com.fintrainer.fintrainer.utils.Constants.EXAM_INTENT
import com.fintrainer.fintrainer.utils.Constants.RANDOM_TESTS_COUNT
import com.fintrainer.fintrainer.utils.Constants.TESTING_INTENT
import com.fintrainer.fintrainer.utils.billing.Inventory
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
import io.realm.annotations.RealmModule
import java.util.*

/**
 * Created by krotk on 23.10.2017.
 */
class RealmContainer {

    private lateinit var sharedPreferences: SharedPreferences

    private val fk = "�e�\\u001Dj�4\\u0016P\\u0003h:���aa"
    private val fk2 = "{b����\\u0002"

    private lateinit var statisticConf: RealmConfiguration
    private lateinit var chapterStatisticsConf: RealmConfiguration
//    private var discussionsConfig: RealmConfiguration? = null
//    private var discussionsRealm: Realm? = null

    fun initSharedPrefrences(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

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

    fun initChapterStatisticsConf() {
        chapterStatisticsConf = RealmConfiguration.Builder()
                .name("csc")
                .modules(ChapterStatisticsModule())
                .schemaVersion(1)
                .build()
    }

    fun initStatisticsRealm() {
        statisticConf = RealmConfiguration.Builder()
                .name("q")
                .modules(Statistics())
                .schemaVersion(2)
                .migration(Migration())
                .build()
    }

    fun saveInAppPurchases(purchases: SparseArray<PurchaseStructDto>, inv: Inventory) {

        Realm.getInstance(statisticConf).use {
            it.executeTransaction {
                val purchases = RealmList<PurchaseStructDto>()
                (0..7).forEach {
                    val purchase = PurchaseStructDto()
                    when (it) {
                        0 -> {
                            purchase.type = 0
                            purchase.purchaseId = "full_basic_test"
                            purchase.description = inv.getSkuDetails("full_basic_test").description
                            purchase.price = inv.getSkuDetails("full_basic_test").price
                            purchase.hasPurchased = inv.hasPurchase("full_basic_test")
                        }
                        1 -> {
                            purchase.type = 1
                            purchase.purchaseId = "full_serial_1_test"
                            purchase.description = inv.getSkuDetails("full_serial_1_test").description
                            purchase.price = inv.getSkuDetails("full_serial_1_test").price
                            purchase.hasPurchased = inv.hasPurchase("full_serial_1_test")
                        }
                        2 -> {
                            purchase.type = 2
                            purchase.purchaseId = "full_serial_2_test"
                            purchase.description = inv.getSkuDetails("full_serial_2_test").description
                            purchase.price = inv.getSkuDetails("full_serial_2_test").price
                            purchase.hasPurchased = inv.hasPurchase("full_serial_2_test")
                        }
                        3 -> {
                            purchase.type = 3
                            purchase.purchaseId = "full_serial_3_test"
                            purchase.description = inv.getSkuDetails("full_serial_3_test").description
                            purchase.price = inv.getSkuDetails("full_serial_3_test").price
                            purchase.hasPurchased = inv.hasPurchase("full_serial_3_test")
                        }
                        4 -> {
                            purchase.type = 4
                            purchase.purchaseId = "full_serial_4_test"
                            purchase.description = inv.getSkuDetails("full_serial_4_test").description
                            purchase.price = inv.getSkuDetails("full_serial_4_test").price
                            purchase.hasPurchased = inv.hasPurchase("full_serial_4_test")
                        }
                        5 -> {
                            purchase.type = 5
                            purchase.purchaseId = "full_serial_5_test"
                            purchase.description = inv.getSkuDetails("full_serial_5_test").description
                            purchase.price = inv.getSkuDetails("full_serial_5_test").price
                            purchase.hasPurchased = inv.hasPurchase("full_serial_5_test")
                        }
                        6 -> {
                            purchase.type = 6
                            purchase.purchaseId = "full_serial_6_test"
                            purchase.description = inv.getSkuDetails("full_serial_6_test").description
                            purchase.price = inv.getSkuDetails("full_serial_6_test").price
                            purchase.hasPurchased = inv.hasPurchase("full_serial_6_test")
                        }
                        7 -> {
                            purchase.type = 7
                            purchase.purchaseId = "full_serial_7_test"
                            purchase.description = inv.getSkuDetails("full_serial_7_test").description
                            purchase.price = inv.getSkuDetails("full_serial_7_test").price
                            purchase.hasPurchased = inv.hasPurchase("full_serial_7_test")
                        }
                    }
                    purchases.add(it, purchase)
                }
                val inAppPurchases = InAppPurchases()
                inAppPurchases.purchases = purchases
                it.copyToRealm(inAppPurchases)
            }
        }
        getInAppPurchases(purchases)
    }

    fun getInAppPurchases(purchases: SparseArray<PurchaseStructDto>) {
        Realm.getInstance(statisticConf).executeTransaction {
            it.where(InAppPurchases::class.java).findFirst()?.let { inAppPurchases ->
                purchases.run {
                    it.copyFromRealm(inAppPurchases)?.purchases?.forEach{purchase: PurchaseStructDto? -> purchase?.type?.let { it1 -> put(it1, purchase) } }
                }
            }
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
            return ExamStatisticAndInfo(getAverageGrade(examId), getAverageRightAnswersCount(examId), chapterCount
                    ?: 0, questionsCount ?: 0, favouriteQuestionsCount ?: 0)

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

    fun getTestsAsync(examId: Int, purchased: Boolean): List<TestingDto> {
        val testingDtos = ArrayList<TestingDto>()
        Realm.getDefaultInstance().use {
            return if (!purchased) {
                //            if (true) {
                val chaptersCount = it.where(ChapterRealm::class.java).equalTo("type", examId).count()
                (0L until chaptersCount)
                        .map { i -> it.where(TestingDto::class.java).equalTo("type", examId).equalTo("chapter", i + 1).findAll() }
                        .forEach { freeTests ->
                            for (i in 0..4) {
                                freeTests[i]?.let { test -> testingDtos.add(it.copyFromRealm(test)) }
                            }
                        }
                //                        .forEach { freeTests -> (0..4).mapTo(testingDtos) { o -> it.copyFromRealm(freeTests[o]) } }
                testingDtos
            } else {
                randomPurchasedTests(it, RANDOM_TESTS_COUNT, false, examId)
            }
        }
    }

    private fun randomPurchasedTests(realmWithExams: Realm, iterationCount: Int, needWeight: Boolean, examId: Int): List<TestingDto> {
        val testingDtos = ArrayList<TestingDto>()
        var iterationCount = iterationCount
        val count = realmWithExams.where(TestingDto::class.java).equalTo("type", examId).count()
        if (count == 0L) {
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

    fun getByChapterAsync(chapter: Int, examId: Int, purchased: Boolean): List<TestingDto> {
        var testingDtos: MutableList<TestingDto> = ArrayList()
        Realm.getDefaultInstance().use {
            val freeTests = it.where(TestingDto::class.java).equalTo("type", examId).equalTo("chapter", chapter).findAll()
            if (!freeTests.isEmpty()) {
                if (!purchased) {
//                if (false) {
                    (0..4).mapTo(testingDtos) { i -> it.copyFromRealm<TestingDto>(freeTests[i]) }
                } else {
                    testingDtos = it.copyFromRealm<TestingDto>(freeTests)
                }
//                shuffleAnswers(testingDtos)
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
            shuffleAnswers(questions)
            return questions
        }
    }

    private fun shuffleAnswers(testingDtos: List<TestingDto>?) {
        if (sharedPreferences.getBoolean("key_shuffle_answers", false)) {
            if (testingDtos != null) {
                for (testingDto in testingDtos) {
                    testingDto.answers!!.shuffle()
                }
            }
        }
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

    fun clearStatistics(examType: Int): String {
        var message = ""
        Realm.getInstance(statisticConf).use {
            it.executeTransaction {
                val results = it.where(AverageGradeStatisticDto::class.java).equalTo("testType", examType).findAll()
                message = if (results.isEmpty()) {
                    "Статистика не найдена"
                } else {
                    results.deleteAllFromRealm()
                    "Статистика очищена"
                }
            }
        }
        return message
    }

    fun clearFavourite(examType: Int): String {
        var message = ""
        Realm.getInstance(statisticConf).use {
            it.executeTransaction {
                val results = it.where(FavouriteQuestionsDto::class.java).equalTo("type", examType).findAll()
                if (results.isEmpty()) {
                    message = "В избранном ничего не найдено"
                } else {
                    results.deleteAllFromRealm()
                    message = "Избранное очищено"
                }
            }
        }
        return message
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

    fun autoRemoveFromFavourite(type: Int, index: Int) {
        Realm.getInstance(statisticConf).use {
            it.executeTransaction {
                val results = it.where(FavouriteQuestionsDto::class.java).equalTo("type", type).equalTo("index", index).findAll()
                if (!results.isEmpty()) {
                    results.deleteAllFromRealm()
                }
            }
        }
    }

    fun getChapterStatisticsForExam(index: Int, chapter: Int): List<ChapterStatistics>? {
        var chapterStatistics: List<ChapterStatistics>? = emptyList()
        Realm.getInstance(chapterStatisticsConf).use {
            chapterStatistics = it.copyFromRealm(it.where(ChapterStatistics::class.java)
                    .equalTo("index", index)
                    .equalTo("chapter", chapter)
                    .findAll())
        }
        return chapterStatistics
    }

    fun addChapterStatisticsForChapter(code: String, index: Int, clickedAnswer: Int, chapter: Int) {
        Realm.getInstance(chapterStatisticsConf).use {
            it.executeTransaction({ realm ->
                var chapterStatistics = realm.where(ChapterStatistics::class.java)
                        .equalTo("index", index)
                        .equalTo("chapter", chapter)
                        .equalTo("code", code)
                        .findFirst()

                if (chapterStatistics == null) {
                    chapterStatistics = ChapterStatistics()
                }
                chapterStatistics.chapter = chapter
                chapterStatistics.index = index
                chapterStatistics.clickedAnswer = clickedAnswer
                chapterStatistics.code = code

                realm.copyToRealm(chapterStatistics)
            })
        }
    }

    fun checkChapterStatisticForExist(index: Int, chapter: Int): Boolean {
        var isExist = false
        Realm.getInstance(chapterStatisticsConf).use {
            isExist = it.where(ChapterStatistics::class.java)
                    .equalTo("index", index)
                    .equalTo("chapter", chapter).count() > 0
        }
        return isExist
    }

    fun deleteChapterStatistics(index: Int, chapter: Int) {
        Realm.getInstance(chapterStatisticsConf).use {
            it.executeTransaction({ realm ->
                val chapterStatistics = realm.where(ChapterStatistics::class.java)
                        .equalTo("index", index)
                        .equalTo("chapter", chapter)
                        .findAll()

                chapterStatistics?.deleteAllFromRealm()
            })
        }
    }

    @RealmModule(classes = [(ExamDto::class), (TestingDto::class), (ChapterRealm::class), (AnswersDto::class)])
    private inner class Default

    @RealmModule(classes = [(CorrectedTestDto::class), (AverageGradeStatisticDto::class), (FavouriteQuestionsDto::class), (InAppPurchases::class), (PurchaseStructDto::class)])
    private inner class Statistics

    @RealmModule(classes = [(ChapterStatistics::class)])
    private inner class ChapterStatisticsModule
}