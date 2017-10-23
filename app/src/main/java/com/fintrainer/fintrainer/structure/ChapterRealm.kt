package com.fintrainer.fintrainer.structure

import io.realm.RealmObject

/**
 * Created by krotk on 29.04.2017.
 */

open class ChapterRealm : RealmObject(){
    var chapterName: String? = null
    var chapterNumber: Int? = null
    var testsCount: Long? = null
    var type: Int? = null
}
