package com.fintrainer.fintrainer.views

import android.app.Application
import com.fintrainer.fintrainer.di.components.AppComponent
import com.fintrainer.fintrainer.di.components.DaggerAppComponent
import com.fintrainer.fintrainer.di.components.DrawerComponent
import com.fintrainer.fintrainer.di.components.TestingComponent
import com.fintrainer.fintrainer.di.modules.AppModule
import com.fintrainer.fintrainer.di.modules.DrawerModule
import com.fintrainer.fintrainer.di.modules.TestingModule
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
        private var testingComponent: TestingComponent? = null

        @JvmStatic
        lateinit var appComponent: AppComponent

        @JvmStatic
        fun initDrawerComponent(): DrawerComponent? {
            if (drawerComponent == null) {
                drawerComponent = appComponent.subDrawerComponent(DrawerModule())
            }
            return drawerComponent
        }

        @JvmStatic
        fun releaseDrawerComponent() {
            drawerComponent = null
        }

        @JvmStatic
        fun initTestingComponent(): TestingComponent? {
            if (testingComponent == null){
                testingComponent = appComponent.subTestingComponent(TestingModule())
            }
            return testingComponent
        }

        @JvmStatic
        fun releaseTestingComponent(){
            testingComponent = null
        }
    }

    @Inject
    lateinit var realmContainer: RealmContainer

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        appComponent.inject(this)
        realmContainer.initRealm()
        realmContainer.initStatisticsRealm()
    }
}