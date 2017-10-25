package com.fintrainer.fintrainer.di.components

import com.fintrainer.fintrainer.di.modules.*
import com.fintrainer.fintrainer.views.App
import dagger.Component
import javax.inject.Singleton

/**
 * Created by krotk on 23.10.2017.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(app: App)

    fun subDrawerComponent(drawerModule: DrawerModule): DrawerComponent
    fun subTestingComponent(testingModule: TestingModule): TestingComponent
    fun subChapterComponent(chapterModule: ChapterModule): ChapterComponent
    fun subSearchComponent(searchModule: SearchModule): SearchComponent
}