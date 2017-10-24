package com.fintrainer.fintrainer.utils.realm

import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.*
import com.fintrainer.fintrainer.utils.Constants.EXAM_INTENT
import com.fintrainer.fintrainer.utils.Constants.TESTING_INTENT
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.annotations.RealmModule
import java.util.*

/**
 * Created by krotk on 23.10.2017.
 */
class RealmContainer {

    private val fk = "�e�\\u001Dj�4\\u0016P\\u0003h:���aa"
    private val fk2 = "{b����\\u0002"

    private lateinit var statisticConf: RealmConfiguration

    fun initRealm() {
        val bArray: ByteArray = (fk + fk2).toByteArray()
        println("Byte array key $bArray")
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

    fun getExamInformation(examId: Int): ExamStatisticAndInfo {
        val realmWithExams = Realm.getDefaultInstance()
        val realmWithStatistics = Realm.getInstance(statisticConf)

        val chapterCount: Int? = realmWithExams.where(ChapterRealm::class.java).equalTo("type", examId).count().toInt()
        val questionsCount: Int? = realmWithExams.where(TestingDto::class.java).equalTo("type", examId).count().toInt()
        val favouriteQuestionsCount: Int? = realmWithStatistics.where(FavouriteQuestionsDto::class.java).equalTo("type", examId).count().toInt()

        realmWithExams.close()
        realmWithStatistics.close()
        return ExamStatisticAndInfo(getAverageGrade(examId), getAverageRightAnswersCount(examId), chapterCount ?: 0, questionsCount ?: 0, favouriteQuestionsCount ?: 0)
    }

    private fun getAverageGrade(examId: Int): Int {
        val realmWithStatistics = Realm.getInstance(statisticConf)

        val averageGrade = realmWithStatistics.where(AverageGradeStatisticDto::class.java)
                .equalTo("examType", EXAM_INTENT).equalTo("testType", examId).findAll()
        var average: Int? = 0
        if (!averageGrade.isEmpty()) {
            var gradesSum: Int? = 0
            val gradesCount = averageGrade.size
            for (gradeStatisticDto in averageGrade) {
                gradesSum = gradesSum!! + gradeStatisticDto.grade!!
            }
            average = gradesSum!! / gradesCount
        }

        realmWithStatistics.close()
        return average ?: 0
    }

    private fun getAverageRightAnswersCount(examId: Int): Int {
        val realmWithStatistics = Realm.getInstance(statisticConf)

        val averageRightAnswers = realmWithStatistics.where(AverageGradeStatisticDto::class.java)
                .equalTo("examType", TESTING_INTENT).equalTo("testType", examId).findAll()
        var average: Int? = 0
        if (!averageRightAnswers.isEmpty()) {
            var answersSum: Int? = 0
            val gradesCount = averageRightAnswers.size
            for (gradeStatisticDto in averageRightAnswers) {
                answersSum = answersSum!! + gradeStatisticDto.rightAnswersCount!!
            }
            average = answersSum!! / gradesCount
        }
        realmWithStatistics.close()
        return average!!
    }

    fun getTestsAsync(intentId: Int, examId: Int): List<TestingDto> {
        val tests = randomTests(intentId == EXAM_INTENT, examId)
        shuffleAnswers(tests)
        return tests
    }

    private fun randomTests(needWeight: Boolean, examId: Int): List<TestingDto> {
        val realmWithExams = Realm.getDefaultInstance()
        realmWithExams.use { realmWithExams ->
            val testingDtos = ArrayList<TestingDto>()
            val count = realmWithExams.where(TestingDto::class.java).equalTo("type", examId).count()
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
                val testingDto = realmWithExams.where(TestingDto::class.java).equalTo("type", examId).equalTo("index", list[i]).findFirst()
                if (testingDto != null) {
                    testingDtos.add(realmWithExams.copyFromRealm(testingDto))
                    i = if (needWeight) testingDto.weight!! + i else i + 1
                }
            }
            return testingDtos
        }
    }

    private fun shuffleAnswers(testingDtos: List<TestingDto>?) {
//        if (sharedPreferences.getBoolean("key_shuffle_answers", false)) {
//            for (testingDto in testingDtos) {
//                Collections.shuffle(testingDto.answers!!)
//            }
//        }
    }

    @RealmModule(classes = arrayOf(ExamDto::class, TestingDto::class, ChapterRealm::class, AnswersDto::class))
    private inner class Default

    @RealmModule(classes = arrayOf(CorrectedTestDto::class, AverageGradeStatisticDto::class, FavouriteQuestionsDto::class))
    inner class Statistics
}