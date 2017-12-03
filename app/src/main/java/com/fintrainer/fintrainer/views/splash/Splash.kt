package com.fintrainer.fintrainer.views.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fintrainer.fintrainer.utils.containers.InAppPurchaseContainer
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.drawer.DrawerActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class Splash : AppCompatActivity() {

    @Inject
    lateinit var inAppPurchaseContainer: InAppPurchaseContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)

        doAsync {
            Thread.sleep(100)
            uiThread {
//                startActivity<DrawerActivity>()
                inAppPurchaseContainer.initPurchases(this@Splash)
            }
        }
    }
}
