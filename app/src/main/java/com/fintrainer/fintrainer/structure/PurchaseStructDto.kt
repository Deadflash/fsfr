package com.fintrainer.fintrainer.structure

import io.realm.RealmObject

/**
 * Created by DoubleFerox on 10.05.2017.
 */

open class PurchaseStructDto : RealmObject(){
    var type: Int? = null
    var purchaseId: String? = null
    var description: String? = null
    var price: String? = null
    var hasPurchased: Boolean = false
}
