package com.fintrainer.fintrainer.di.components

import com.fintrainer.fintrainer.di.modules.TestingModule
import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.views.result.ResultActivity
import com.fintrainer.fintrainer.views.testing.TestingActivity
import com.fintrainer.fintrainer.views.testing.fragments.TestingFragment
import dagger.Subcomponent

/**
 * Created by krotk on 24.10.2017.
 */

@PerActivity
@Subcomponent(modules = arrayOf(TestingModule::class))
interface TestingComponent {
    fun inject(testingActivity: TestingActivity)
    fun inject(testingFragment: TestingFragment)
    fun inject(resultActivity: ResultActivity)
}