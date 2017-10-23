package com.fintrainer.fintrainer.structure

import io.realm.DynamicRealm
import io.realm.RealmMigration
import io.realm.RealmSchema

/**
 * Created by deadf on 12.06.2017.
 */

class Migration : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion
        val schema = realm.schema
        //        System.out.println("OLD Version Realm "+ oldVersion);
        if (oldVersion == 1L) {
            schema.create("FavouriteQuestionsDto")
                    .addField("index", Long::class.java)
                    .addField("chapter", Int::class.java)
                    .addField("type", Int::class.java)
            oldVersion++
        }
        //        System.out.println("New Version Realm "+ oldVersion);
    }
}
