package com.fintrainer.fintrainer.di.modules

import android.content.Context
import com.fintrainer.fintrainer.utils.PicassoContainer
import com.fintrainer.fintrainer.utils.realm.RealmContainer
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by krotk on 23.10.2017.
 */
@Module
class AppModule(val context: Context) {

    @Provides
    @Singleton
    fun provideRealm(): RealmContainer = RealmContainer()

    @Provides
    @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun providePicasso(context: Context): PicassoContainer = PicassoContainer(context)
}