package com.fintrainer.fintrainer.structure

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DoubleFerox on 30.08.2017.
 */

open class DiscussionRateDto: RealmObject(){
        @PrimaryKey
        var id: String? = null
        var parent: DiscussionQuestionDto? = null
        var userId: String? = null
        var direction: Boolean? = null
}
