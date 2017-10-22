package com.fcpunlimited.fsfr.di.modules

import com.fcpunlimited.fsfr.di.scopes.PerFragment
import com.fcpunlimited.fsfr.views.drawer.fragments.MainDrawerFragmentPresenter
import dagger.Module
import dagger.Provides

/**
 * Created by krotk on 21.10.2017.
 */
@Module
class MainDrawerFragmentModule {

    @Provides
    @PerFragment
    fun addMainDrawerPresenter(): MainDrawerFragmentPresenter = MainDrawerFragmentPresenter()
}