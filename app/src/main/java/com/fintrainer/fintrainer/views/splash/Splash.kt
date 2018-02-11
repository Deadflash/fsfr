package com.fintrainer.fintrainer.views.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fintrainer.fintrainer.utils.containers.InAppPurchaseContainer
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.drawer.DrawerActivity
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class Splash : AppCompatActivity(), InAppPurchaseContainer.SplashStartApp {

    @Inject
    lateinit var inAppPurchaseContainer: InAppPurchaseContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)

        inAppPurchaseContainer.initSplash(this@Splash)
        inAppPurchaseContainer.initPurchases(this@Splash)
    }

    override fun startApp() {
        startActivity<DrawerActivity>()
    }

    override fun onDestroy() {
        super.onDestroy()
        inAppPurchaseContainer.releaseSplash()
    }
}
