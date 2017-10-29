package com.fintrainer.fintrainer.di.components

import com.fintrainer.fintrainer.di.modules.DiscussionsModule
import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.views.discussions.DiscussionsActivity
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentAddDiscussion
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentComments
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentDiscussions
import dagger.Subcomponent

/**
 * Created by krotk on 28.10.2017.
 */
@PerActivity
@Subcomponent(modules = arrayOf(DiscussionsModule::class))
interface DiscussionComponent {
    fun inject(discussionsActivity: DiscussionsActivity)
    fun inject(fragmentDiscussions: FragmentDiscussions)
    fun inject(fragmentComments: FragmentComments)
    fun inject(fragmentAddDiscussion: FragmentAddDiscussion)
}