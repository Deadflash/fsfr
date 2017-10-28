package com.fintrainer.fintrainer.di.modules

import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.utils.RealmContainer
import com.fintrainer.fintrainer.views.search.SearchPresenter
import dagger.Module
import dagger.Provides

/**
 * Created by krotk on 25.10.2017.
 */
@Module
class SearchModule {

    @Provides
    @PerActivity
    fun addSearchPresenter(realmContainer: RealmContainer) = SearchPresenter(realmContainer)
}