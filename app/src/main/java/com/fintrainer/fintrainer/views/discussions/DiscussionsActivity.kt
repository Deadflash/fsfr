package com.fintrainer.fintrainer.views.discussions

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.di.contracts.DiscussionsContract
import com.fintrainer.fintrainer.utils.Constants.RC_SIGN_IN
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentDiscussions
import org.jetbrains.anko.toast
import javax.inject.Inject

class DiscussionsActivity : AppCompatActivity(), DiscussionsContract.View {

    @Inject
    lateinit var presenter: DiscussionsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)
        App.initDiscussionsComponent()?.inject(this)
        presenter.bind(this)
        presenter.initDiscussionsRealm()

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentDiscussions()).commit()
        }

    }

    override fun realmStatus(code: Int) {
        toast("CODE: $code")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> presenter.handleAuthResult(requestCode, resultCode, data!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unBind()
    }
}
