package com.fcpunlimited.fsfr.views.drawer

import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.fcpunlimited.fsfr.R
import com.fcpunlimited.fsfr.views.BaseActivity
import com.fcpunlimited.fsfr.views.drawer.fragments.MainDrawerFragment
import com.readystatesoftware.systembartint.SystemBarTintManager
import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class DrawerActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        initStatusBar()
        setupToolbar()
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, MainDrawerFragment()).commit()
        }
    }

    private fun initStatusBar() {
        val window = window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            val tintManager = SystemBarTintManager(this)
            tintManager.isStatusBarTintEnabled = true
            tintManager.setTintColor(ContextCompat.getColor(this, R.color.blue_grey_700))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = ContextCompat.getColor(this, R.color.blue_grey_700)
        }


    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        val toggle = object : ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
//                toast("Closed")
            }
        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigation_view.setNavigationItemSelectedListener(this)
        navigation_view.getHeaderView(0)?.ivLogout?.onClick { toast("logout") }
        navigation_view.menu?.getItem(0)?.isChecked = true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_base_exam -> {
                toast("base")
                true
            }
            R.id.nav_1_serial_exam -> {
                toast("serial 1")
                true
            }
            R.id.nav_2_serial_exam -> {
                toast("serial 2")
                true
            }
            R.id.nav_3_serial_exam -> {
                toast("serial 3")
                true
            }
            R.id.nav_4_serial_exam -> {
                toast("serial 4")
                true
            }
            R.id.nav_5_serial_exam -> {
                toast("serial 5")
                true
            }
            R.id.nav_6_serial_exam -> {
                toast("serial 6")
                true
            }
            R.id.nav_7_serial_exam -> {
                toast("serial 7")
                true
            }
            else -> return false
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
