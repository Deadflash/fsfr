package com.fintrainer.fintrainer.views

import android.app.Application
import com.fintrainer.fintrainer.di.components.DaggerDrawerComponent
import com.fintrainer.fintrainer.di.components.DrawerComponent

/**
 * Created by krotk on 21.10.2017.
 */
class App : Application() {

    companion object {
        @JvmStatic
        lateinit var component: DrawerComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerDrawerComponent.create()
    }
}