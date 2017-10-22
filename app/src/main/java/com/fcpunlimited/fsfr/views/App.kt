package com.fcpunlimited.fsfr.views

import android.app.Application
import com.fcpunlimited.fsfr.di.components.DaggerMainDrawerFragmentComponent
import com.fcpunlimited.fsfr.di.components.MainDrawerFragmentComponent

/**
 * Created by krotk on 21.10.2017.
 */
class App : Application() {

    companion object {
        @JvmStatic
        lateinit var mainComponent : MainDrawerFragmentComponent
    }

    override fun onCreate() {
        super.onCreate()
        mainComponent = DaggerMainDrawerFragmentComponent.create()
    }
}