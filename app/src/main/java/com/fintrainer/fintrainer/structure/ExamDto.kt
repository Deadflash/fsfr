package com.fintrainer.fintrainer.structure

import io.realm.RealmObject

/**
 * Created by DoubleFerox on 09.05.2017.
 */

open class ExamDto: RealmObject(){
    var examType: Int? = null
    var examName: String? = null
    var activated: Boolean? = null
}
