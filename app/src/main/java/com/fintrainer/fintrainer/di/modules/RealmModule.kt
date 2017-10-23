package com.fintrainer.fintrainer.di.modules

import com.fintrainer.fintrainer.utils.realm.RealmContainer
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by krotk on 23.10.2017.
 */
@Module
class RealmModule {

    @Provides
    @Singleton
    fun provideRealm(): RealmContainer = RealmContainer()
}