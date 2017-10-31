package com.fintrainer.fintrainer.views.discussions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.di.contracts.DiscussionsContract
import com.fintrainer.fintrainer.utils.Constants.RC_SIGN_IN
import com.fintrainer.fintrainer.utils.Constants.REALM_AUTH_TOKEN_ERROR
import com.fintrainer.fintrainer.utils.Constants.REALM_FAIL_CONNECT_CODE
import com.fintrainer.fintrainer.utils.Constants.REALM_SUCCESS_CONNECT_CODE
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseActivity
import com.fintrainer.fintrainer.views.BaseFragment
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentAddDiscussion
import com.fintrainer.fintrainer.views.discussions.fragments.FragmentDiscussions
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class DiscussionsActivity : BaseActivity(), DiscussionsContract.View {

    @Inject
    lateinit var presenter: DiscussionsPresenter

    private var fragment: BaseFragment? = null
    private var addDiscussionMenu: MenuItem? = null
    private var createDiscussionMenu: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion)
        App.initDiscussionsComponent()?.inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val purchased = intent.getBooleanExtra("purchased", false)
        val questionId = intent.getStringExtra("questionCode")
        val testType = intent.getIntExtra("testType", -1)

        fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as BaseFragment?
    }

    override fun onStart() {
        super.onStart()
        presenter.bind(this)
        presenter.initDiscussionsRealm()
    }

    override fun realmStatus(code: Int) {

        when (code) {
            Activity.RESULT_CANCELED -> {
                toast(R.string.action_canceled)
            }
            REALM_SUCCESS_CONNECT_CODE -> {
                if (fragment == null) {
                    fragment = FragmentDiscussions()
                    replaceFragment(fragment as FragmentDiscussions)
                    setMenuIconsVisibility(fragment as FragmentDiscussions)
                } else {
                    when (fragment) {
                        fragment as? FragmentDiscussions -> {
                            toast("DISCUSSION")
                        }
                        fragment as? FragmentAddDiscussion -> {
                            toast("ADD DISCUSSION")
                        }
                        else -> {
                            toast("Error fragment")
                        }
                    }
                }
            }
            REALM_FAIL_CONNECT_CODE -> {
                toast(R.string.error_realm_config)
            }
            REALM_AUTH_TOKEN_ERROR -> {
                toast("Realm auth token error")
            }
            else -> {
                toast(R.string.something_went_wrong)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.discussions_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        createDiscussionMenu = menu?.getItem(0)
        addDiscussionMenu = menu?.getItem(1)
        createDiscussionMenu?.isVisible = false
        addDiscussionMenu?.isVisible = false
        fragment?.let { setMenuIconsVisibility(it) }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        R.id.menu_add_discussion -> {
            fragment = FragmentAddDiscussion()
            replaceFragment(fragment as FragmentAddDiscussion)
            setMenuIconsVisibility(fragment as FragmentAddDiscussion)
            true
        }
        R.id.menu_create_discussion -> {
            presenter.onCreateDiscussionClicked()
            true
        }
        else -> false
    }

    private fun setMenuIconsVisibility(fragment: BaseFragment) {
        when (fragment) {
            fragment as? FragmentDiscussions -> {
                createDiscussionMenu?.isVisible = true
                addDiscussionMenu?.isVisible = false
            }
            fragment as? FragmentAddDiscussion -> {
                createDiscussionMenu?.isVisible = false
                addDiscussionMenu?.isVisible = true
            }
        }
    }

    override fun onBackPressed() {
        if ((fragment as? FragmentDiscussions)?.isVisible == true || fragment == null) {
            super.onBackPressed()
        } else {
            fragment = FragmentDiscussions()
            replaceFragment(fragment as FragmentDiscussions)
            setMenuIconsVisibility(fragment as FragmentDiscussions)
        }

    }

    private fun replaceFragment(fragment: BaseFragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> presenter.handleAuthResult(requestCode, resultCode, data!!)
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.unBind()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.closeRealm()
    }
}
