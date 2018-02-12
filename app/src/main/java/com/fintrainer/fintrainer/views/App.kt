package com.fintrainer.fintrainer.views

import android.app.Application
import com.fintrainer.fintrainer.di.components.*
import com.fintrainer.fintrainer.di.modules.*
import com.fintrainer.fintrainer.utils.RateApp
import com.fintrainer.fintrainer.utils.containers.RealmContainer
import io.realm.Realm
import javax.inject.Inject

/**
 * Created by krotk on 21.10.2017.
 */
class App : Application() {

    companion object {

        @JvmStatic
        lateinit var appComponent: AppComponent

        @JvmStatic
        private var drawerComponent: DrawerComponent? = null

        @JvmStatic
        private var testingComponent: TestingComponent? = null

        @JvmStatic
        private var chapterComponent: ChapterComponent? = null

        @JvmStatic
        private var searchComponent: SearchComponent? = null

        @JvmStatic
        private var discussionComponent: DiscussionComponent? = null

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
            if (testingComponent == null) {
                testingComponent = appComponent.subTestingComponent(TestingModule())
            }
            return testingComponent
        }

        @JvmStatic
        fun releaseTestingComponent() {
            testingComponent = null
        }

        @JvmStatic
        fun initChapterComponent(): ChapterComponent? {
            if (chapterComponent == null) {
                chapterComponent = appComponent.subChapterComponent(ChapterModule())
            }
            return chapterComponent
        }

        @JvmStatic
        fun releaseChapterComponent() {
            chapterComponent = null
        }

        @JvmStatic
        fun initSearchComponent(): SearchComponent? {
            if (searchComponent == null) {
                searchComponent = appComponent.subSearchComponent(SearchModule())
            }
            return searchComponent
        }

        @JvmStatic
        fun releaseSearchComponent() {
            searchComponent = null
        }

        @JvmStatic
        fun initDiscussionsComponent(): DiscussionComponent? {
            if (discussionComponent == null) {
                discussionComponent = appComponent.subDiscussionsComponent(DiscussionsModule())
            }
            return discussionComponent
        }

        @JvmStatic
        fun releaseDiscussionsComponent() {
            discussionComponent = null
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
        realmContainer.initChapterStatisticsConf()
        realmContainer.initStatisticsRealm()
        realmContainer.initSharedPrefrences(this)

        RateApp.setupRate(this)
    }
}