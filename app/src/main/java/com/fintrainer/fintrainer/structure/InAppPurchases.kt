package com.fintrainer.fintrainer.structure

import io.realm.RealmList
import io.realm.RealmObject

open class InAppPurchases : RealmObject() {
    lateinit var purchases: RealmList<PurchaseStructDto>
}