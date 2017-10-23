package com.fintrainer.fintrainer.views

import android.app.Application
import com.fintrainer.fintrainer.di.components.AppComponent
import com.fintrainer.fintrainer.di.components.DaggerAppComponent
import com.fintrainer.fintrainer.di.components.DrawerComponent
import com.fintrainer.fintrainer.di.modules.DrawerModule
import com.fintrainer.fintrainer.utils.realm.RealmContainer
import io.realm.Realm
import javax.inject.Inject

/**
 * Created by krotk on 21.10.2017.
 */
class App : Application() {

    companion object {
        @JvmStatic
        private var drawerComponent: DrawerComponent? = null

        @JvmStatic
        lateinit var appComponent: AppComponent

        @JvmStatic
        fun initDrawerComponent(): DrawerComponent? {
            if (drawerComponent == null) {
                drawerComponent = appComponent.subDrawerComponent(DrawerModule())
            }
            return drawerComponent
        }
    }

    @Inject
    lateinit var realmContainer: RealmContainer

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)
//        realmContainer.initRealm()
    }
}