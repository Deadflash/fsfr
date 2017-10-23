package com.fintrainer.fintrainer.structure

import io.realm.RealmObject

/**
 * Created by deadf on 04.05.2017.
 */

open class AverageGradeStatisticDto : RealmObject() {
    var examType: Int? = null
    var grade: Int? = null
    var testType: Int? = null
    var rightAnswersCount: Int? = null
}
