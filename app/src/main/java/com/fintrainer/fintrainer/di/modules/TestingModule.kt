package com.fintrainer.fintrainer.di.modules

import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.utils.realm.RealmContainer
import com.fintrainer.fintrainer.views.testing.TestingPresenter
import dagger.Module
import dagger.Provides

/**
 * Created by krotk on 24.10.2017.
 */
@Module
class TestingModule {

    @Provides
    @PerActivity
    fun addTestingPresenter(realmContainer: RealmContainer): TestingPresenter = TestingPresenter(realmContainer)
}