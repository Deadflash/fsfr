package com.fintrainer.fintrainer.views.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fintrainer.fintrainer.views.drawer.DrawerActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        doAsync {
            Thread.sleep(100)
            uiThread {
                startActivity<DrawerActivity>()
            }
        }
    }
}
