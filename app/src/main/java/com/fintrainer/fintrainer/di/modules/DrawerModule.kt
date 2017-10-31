package com.fintrainer.fintrainer.di.modules

import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.utils.containers.RealmContainer
import com.fintrainer.fintrainer.views.drawer.DrawerPresenter
import dagger.Module
import dagger.Provides

/**
 * Created by krotk on 21.10.2017.
 */
@Module
class DrawerModule {

    @Provides
    @PerActivity
    fun addDrawerPresenter(realmContainer: RealmContainer): DrawerPresenter = DrawerPresenter(realmContainer)
}