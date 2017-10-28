package com.fintrainer.fintrainer.di.modules

import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.utils.GoogleAuthContainer
import com.fintrainer.fintrainer.utils.RealmContainer
import com.fintrainer.fintrainer.views.discussions.DiscussionsPresenter
import dagger.Module
import dagger.Provides

/**
 * Created by krotk on 28.10.2017.
 */
@Module
class DiscussionsModule {

    @Provides
    @PerActivity
    fun provideDiscussionPresenter(realmContainer: RealmContainer, authContainer: GoogleAuthContainer): DiscussionsPresenter
            = DiscussionsPresenter(realmContainer,authContainer)
}