package com.fintrainer.fintrainer.structure

import io.realm.RealmObject

/**
 * Created by deadf on 15.03.2017.
 */

open class AnswersDto : RealmObject(){
    var text: String? = null
    var status: Boolean? = null
    var clicked: Boolean? = null
    var checked: Boolean? = null
}
