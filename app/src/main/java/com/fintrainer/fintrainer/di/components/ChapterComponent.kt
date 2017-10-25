package com.fintrainer.fintrainer.di.components

import com.fintrainer.fintrainer.di.modules.ChapterModule
import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.views.chapters.ChaptersActivity
import dagger.Subcomponent

/**
 * Created by krotk on 25.10.2017.
 */
@PerActivity
@Subcomponent(modules = arrayOf(ChapterModule::class))
interface ChapterComponent {
    fun inject(chaptersActivity: ChaptersActivity)
}