package com.fcpunlimited.fsfr.di.components

import com.fcpunlimited.fsfr.di.modules.MainDrawerFragmentModule
import com.fcpunlimited.fsfr.di.scopes.PerFragment
import com.fcpunlimited.fsfr.views.drawer.DrawerActivity
import com.fcpunlimited.fsfr.views.drawer.fragments.MainDrawerFragment
import dagger.Component

/**
 * Created by krotk on 21.10.2017.
 */

@PerFragment
@Component(modules = arrayOf(MainDrawerFragmentModule::class))
interface MainDrawerFragmentComponent {
    fun inject(mainDrawerFragment: MainDrawerFragment)
    fun inject(drawerActivity: DrawerActivity)
}