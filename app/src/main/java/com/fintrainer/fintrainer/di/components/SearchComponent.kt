package com.fintrainer.fintrainer.di.components

import com.fintrainer.fintrainer.di.modules.SearchModule
import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.views.search.SearchActivity
import dagger.Subcomponent

/**
 * Created by krotk on 25.10.2017.
 */
@PerActivity
@Subcomponent(modules = arrayOf(SearchModule::class))
interface SearchComponent {
    fun inject(searchActivity: SearchActivity)
}