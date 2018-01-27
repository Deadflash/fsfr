package com.fintrainer.fintrainer.structure.chapters

import io.realm.RealmObject

/**
 * Created by deadf on 27.01.2018.
 */
open class ChapterStatistics: RealmObject(){
    var index: Int? = null
    var chapter: Int? = null
    var code: String? = null
    var clickedAnswer: Int? = null
}