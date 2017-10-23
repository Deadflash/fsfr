package com.fintrainer.fintrainer.structure

/**
 * Created by DoubleFerox on 10.05.2017.
 */

data class PurchaseStructDto(
        var purchaseId: String,
        var Description: String,
        var price: String,
        var hasPurchased: Boolean
)
