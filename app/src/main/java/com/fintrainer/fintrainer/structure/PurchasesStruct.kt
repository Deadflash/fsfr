package com.fintrainer.fintrainer.structure

/**
 * Created by krotk on 21.10.2017.
 */
data class PurchasesStruct(
        var purchaseId: String,
        var Description: String,
        var price: String,
        var hasPurchased: Boolean
)