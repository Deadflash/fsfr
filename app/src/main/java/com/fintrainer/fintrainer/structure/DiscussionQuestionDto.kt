package com.fintrainer.fintrainer.structure

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by DoubleFerox on 30.08.2017.
 */

open class DiscussionQuestionDto : RealmObject(){
        @PrimaryKey
        var id: String? = null
        var questionId: String? = null
        var questionType: Int? = null
        var discussionCreator: String? = null
        var text: String? = null
        var commentList: RealmList<DiscussionCommentDto>? = null
        var rateList: RealmList<DiscussionRateDto>? = null
}
