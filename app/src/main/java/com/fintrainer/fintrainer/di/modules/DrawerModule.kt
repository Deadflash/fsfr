package com.fintrainer.fintrainer.di.modules

import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.views.drawer.fragments.DrawerPresenter
import dagger.Module
import dagger.Provides

/**
 * Created by krotk on 21.10.2017.
 */
@Module
class DrawerModule {

    @Provides
    @PerActivity
    fun addDrawerPresenter(): DrawerPresenter = DrawerPresenter()
}