package com.fintrainer.fintrainer.di.modules

import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.utils.realm.RealmContainer
import com.fintrainer.fintrainer.views.chapters.ChaptersPresenter
import com.fintrainer.fintrainer.views.testing.TestingPresenter
import dagger.Module
import dagger.Provides

/**
 * Created by krotk on 25.10.2017.
 */
@Module
class ChapterModule{

    @Provides
    @PerActivity
    fun addChapterModule(realmContainer: RealmContainer): ChaptersPresenter = ChaptersPresenter(realmContainer)
}