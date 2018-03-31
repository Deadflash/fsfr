package com.fintrainer.fintrainer.structure

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration


class PurchaseMigration : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion
        val schema = realm.schema
        println("Old schema: $oldVersion, new schema: $newVersion")
        if (oldVersion == 1L) {
            val purchaseStruct = schema.create("PurchaseStructDto")
                    .addField("type", Int::class.java)
                    .addField("purchaseId", String::class.java)
                    .addField("description", String::class.java)
                    .addField("price", String::class.java)
                    .addField("hasPurchased", Boolean::class.java)
            schema.create("InAppPurchases")
                    .addRealmListField("purchases", purchaseStruct)
            oldVersion++
        }
        println("After Migration old schema: $oldVersion, new schema: $newVersion")
    }
}