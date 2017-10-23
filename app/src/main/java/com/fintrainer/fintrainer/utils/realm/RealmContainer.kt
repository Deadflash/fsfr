package com.fintrainer.fintrainer.utils.realm

import com.fintrainer.fintrainer.structure.AnswersDto
import com.fintrainer.fintrainer.structure.ChapterRealm
import com.fintrainer.fintrainer.structure.ExamDto
import com.fintrainer.fintrainer.structure.TestingDto
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.annotations.RealmModule

/**
 * Created by krotk on 23.10.2017.
 */
class RealmContainer {

    private val fk = "�e�\\u001Dj�4\\u0016P\\u0003h:���aa"
    private val fk2 = "{b����\\u0002"

    fun initRealm() {
        val bArray: ByteArray = (fk + fk2).toByteArray()
        println("Byte array key $bArray")
        val config = RealmConfiguration.Builder()
                .name("n")
                .assetFile("n") // e.g "default.realm" or "lib/data.realm"
                .modules(Default())
                .encryptionKey((fk + fk2).toByteArray())
                .schemaVersion(1)
                .build()

//        Realm.compactRealm(config);
        Realm.setDefaultConfiguration(config)

        val realm: Realm = Realm.getDefaultInstance()
        val count = realm.where(TestingDto::class.java).equalTo("type", 0L).count()
        println("Count $count")
    }

    @RealmModule(classes = arrayOf(ExamDto::class, TestingDto::class, ChapterRealm::class, AnswersDto::class))
    private inner class Default

}