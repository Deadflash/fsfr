package com.fintrainer.fintrainer.structure

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by krotk on 16.10.2017.
 */

open class ApprovedCommentsDto : RealmObject(){
        @PrimaryKey
        var id: String? = null
        var questionCode: String? = null
        var comment: String? = null
        var commentCreator: String? = null
}
