package com.fintrainer.fintrainer.structure

import io.realm.RealmObject

/**
 * Created by krotk on 01.05.2017.
 */

open class CorrectedTestDto : RealmObject(){
    var testIndex: Long? = null
    var testType: Int? = null
    var testChapter: Int? = null
    var corrected: Boolean? = null
}
