package com.fintrainer.fintrainer.di.modules

import com.fintrainer.fintrainer.di.scopes.PerActivity
import com.fintrainer.fintrainer.utils.containers.DiscussionsSyncRealmContainer
import com.fintrainer.fintrainer.utils.containers.GoogleAuthContainer
import com.fintrainer.fintrainer.utils.containers.RealmContainer
import com.fintrainer.fintrainer.views.testing.TestingPresenter
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.Module
import dagger.Provides

/**
 * Created by krotk on 24.10.2017.
 */
@Module
class TestingModule {

    @Provides
    @PerActivity
    fun addTestingPresenter(realmContainer: RealmContainer,
                            discussionsSyncRealmContainer: DiscussionsSyncRealmContainer,
                            googleAuthContainer: GoogleAuthContainer): TestingPresenter = TestingPresenter(realmContainer,discussionsSyncRealmContainer,googleAuthContainer)
}