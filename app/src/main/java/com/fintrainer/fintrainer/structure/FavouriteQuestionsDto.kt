package com.fintrainer.fintrainer.structure

import io.realm.RealmObject

/**
 * Created by deadf on 12.06.2017.
 */


open class FavouriteQuestionsDto: RealmObject(){
    var index: Long? = null
    var chapter: Int? = null
    var type: Int? = null
}
