package com.fintrainer.fintrainer.di.components

import com.fintrainer.fintrainer.di.modules.DrawerModule
import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.views.drawer.DrawerActivity
import dagger.Component
import dagger.Subcomponent

/**
 * Created by krotk on 21.10.2017.
 */

@PerActivity
@Subcomponent(modules = arrayOf(DrawerModule::class))
interface DrawerComponent {
    fun inject(drawerActivity: DrawerActivity)
}