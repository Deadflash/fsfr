package com.fintrainer.fintrainer.di.components

import com.fintrainer.fintrainer.di.modules.RealmModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by krotk on 23.10.2017.
 */
@Singleton
@Component(modules = arrayOf(RealmModule::class))
interface AppComponent {

}