package com.fintrainer.fintrainer.structure

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DoubleFerox on 30.08.2017.
 */

open class DiscussionCommentDto : RealmObject() {
    @PrimaryKey
    var id: String? = null
    var parent: DiscussionQuestionDto? = null
    var text: String? = null
    var commentCreator: String? = null
    var rateList: RealmList<DiscussionRateDto>? = null
}
