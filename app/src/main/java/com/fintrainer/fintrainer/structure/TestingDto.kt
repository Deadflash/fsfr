package com.fintrainer.fintrainer.structure

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore

/**
 * Created by deadf on 15.03.2017.
 */

open class TestingDto : RealmObject(){
        var index: Long? = null
        var weight: Int? = null
        var task: String? = null
        var code: String? = null
        var chapter: Int? = null
        var title: String? = null
        var image: String? = null
        var type: Int? = null
        @Ignore
        var rightAnswer: Boolean? = null
        var corrected: Boolean? = null
        var answers: RealmList<AnswersDto>? = null
}
