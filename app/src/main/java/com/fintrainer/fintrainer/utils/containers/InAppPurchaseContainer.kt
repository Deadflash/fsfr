package com.fintrainer.fintrainer.utils.containers

import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.SparseArray
import collections.forEach
import com.fintrainer.fintrainer.structure.PurchaseStructDto
import com.fintrainer.fintrainer.utils.Constants.EXAM_BASE
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_1
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_2
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_3
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_4
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_5
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_6
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_7
import com.fintrainer.fintrainer.utils.billing.IabHelper
import com.fintrainer.fintrainer.utils.billing.SkuDetails
import com.fintrainer.fintrainer.views.BaseActivity
import com.fintrainer.fintrainer.views.drawer.DrawerActivity
import com.fintrainer.fintrainer.views.splash.Splash
import java.util.*

/**
 * Created by krotk on 03.12.2017.
 */
class InAppPurchaseContainer {

    private val TAG = InAppPurchaseContainer::class.java.name


    private var mPurchaseFinishedListener: IabHelper.OnIabPurchaseFinishedListener? = null
    private var skuDetails: SkuDetails? = null
    private var mHelper: IabHelper? = null
    private val base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo1e+hjuC8QlVO4HZM2/D5W/Mn26L1bW2edBJs2MEO6CFqnRrVjkm7wAW5NXq7HYpG4pyM1W3fz+aPxIO7zcNEEiNq9k7NaMjphe3Whhvc5ro8O43xEGjTQ+PVoDAPx3JepkU9yM0FQ8lgvWs5UFZ1S1lmMbt1DIUOFz3kXml/vR6D3NCjk8XNq2H0HdZSErnulnlZrhBAyf7KAH+PoNkFcd7ADxVtwzPsNhJmsN9WVEk6tInTouiRYITDKl7VAA3n3+TbmCKpsYN7A5O5PkwMrbRaTGHbbAABShaDl9fjBYquS8uCF23lLUTLHGlSIaC5pUMFPm/5Vcbg7ahvUF8YwIDAQAB"
    private val purchases: SparseArray<PurchaseStructDto> = SparseArray()
    private var splash: SplashStartApp? = null
    private var drawer: DrawerInterface? = null

    fun initSplash(splash: Splash) {
        this.splash = splash
    }

    fun initDrawer(drawer: DrawerActivity) {
        this.drawer = drawer
    }

    fun releaseDrawer() {
        this.drawer = null
    }

    fun releaseSplash() {
        this.splash = null
    }

    fun getPurchases(): SparseArray<PurchaseStructDto> = purchases

    fun checkIsPurchasedExam(position: Int): Boolean = purchases.get(position).hasPurchased

    fun initPurchases(context: Context) {
        if (mHelper == null) {
            purchases.put(EXAM_BASE, PurchaseStructDto("full_basic_test", "", "0.0", false))
            purchases.put(EXAM_SERIAL_1, PurchaseStructDto("full_serial_1_test", "", "0.0", false))
            purchases.put(EXAM_SERIAL_2, PurchaseStructDto("full_serial_2_test", "", "0.0", false))
            purchases.put(EXAM_SERIAL_3, PurchaseStructDto("full_serial_3_test", "", "0.0", false))
            purchases.put(EXAM_SERIAL_4, PurchaseStructDto("full_serial_4_test", "", "0.0", false))
            purchases.put(EXAM_SERIAL_5, PurchaseStructDto("full_serial_5_test", "", "0.0", false))
            purchases.put(EXAM_SERIAL_6, PurchaseStructDto("full_serial_6_test", "", "0.0", false))
            purchases.put(EXAM_SERIAL_7, PurchaseStructDto("full_serial_7_test", "", "0.0", false))
            mHelper = IabHelper(context, base64EncodedPublicKey)

            mHelper?.startSetup({ result ->
                if (!result.isSuccess) {
                    Log.d(TAG, "Problem setting up In-app Billing: " + result)
                }
                try {
                    mHelper?.queryInventoryAsync(true, Arrays.asList("full_basic_test", "full_serial_1_test", "full_serial_2_test", "full_serial_3_test", "full_serial_4_test", "full_serial_5_test", "full_serial_6_test", "full_serial_7_test"),
                            ArrayList(), IabHelper.QueryInventoryFinishedListener { result, inv ->
                        if (result.isFailure) {
                            Log.d(TAG, "Result is failure In-app Billing: " + result)
                            splash?.startApp()
                            return@QueryInventoryFinishedListener
                        }
                        purchases.run {
                            put(EXAM_BASE, PurchaseStructDto("full_basic_test", inv.getSkuDetails("full_basic_test").description, inv.getSkuDetails("full_basic_test").price, inv.hasPurchase("full_basic_test")))
                            put(EXAM_SERIAL_1, PurchaseStructDto("full_serial_1_test", inv.getSkuDetails("full_serial_1_test").description, inv.getSkuDetails("full_serial_1_test").price, inv.hasPurchase("full_serial_1_test")))
                            put(EXAM_SERIAL_2, PurchaseStructDto("full_serial_2_test", inv.getSkuDetails("full_serial_2_test").description, inv.getSkuDetails("full_serial_2_test").price, inv.hasPurchase("full_serial_2_test")))
                            put(EXAM_SERIAL_3, PurchaseStructDto("full_serial_3_test", inv.getSkuDetails("full_serial_3_test").description, inv.getSkuDetails("full_serial_3_test").price, inv.hasPurchase("full_serial_3_test")))
                            put(EXAM_SERIAL_4, PurchaseStructDto("full_serial_4_test", inv.getSkuDetails("full_serial_4_test").description, inv.getSkuDetails("full_serial_4_test").price, inv.hasPurchase("full_serial_4_test")))
                            put(EXAM_SERIAL_5, PurchaseStructDto("full_serial_5_test", inv.getSkuDetails("full_serial_5_test").description, inv.getSkuDetails("full_serial_5_test").price, inv.hasPurchase("full_serial_5_test")))
                            put(EXAM_SERIAL_6, PurchaseStructDto("full_serial_6_test", inv.getSkuDetails("full_serial_6_test").description, inv.getSkuDetails("full_serial_6_test").price, inv.hasPurchase("full_serial_6_test")))
                            put(EXAM_SERIAL_7, PurchaseStructDto("full_serial_7_test", inv.getSkuDetails("full_serial_7_test").description, inv.getSkuDetails("full_serial_7_test").price, inv.hasPurchase("full_serial_7_test")))
                        }
                        Log.d(TAG, "Received purchases : $purchases")
                        splash?.startApp()
                    })
                } catch (e: IabHelper.IabAsyncInProgressException) {
                    Log.w(TAG, "Error while getting billing service. Exception : ", e)
                    splash?.startApp()
                }
            })
            setPurchaseListener()
        }else{
            splash?.startApp()
        }
    }

    fun purchase(activity: BaseActivity, position: Int) {
        try {
            mHelper?.launchPurchaseFlow(activity, purchases.get(position).purchaseId, 10001,
                    mPurchaseFinishedListener, UUID.randomUUID().toString())
        } catch (e: Exception) {
            Log.w(TAG, "Error while purchasing. Exception : ", e)
        }

    }

    fun handlePurchaseResult(requestCode: Int, resultCode: Int, data: Intent) {
        mHelper?.handleActivityResult(requestCode, resultCode, data)
    }

    private fun setPurchaseListener() {
        mPurchaseFinishedListener = IabHelper.OnIabPurchaseFinishedListener(function = { result, purchase ->
            if (result.isFailure) {
                if (result.response == 7) {
                    drawer?.showPurchaseMessage()
                    drawer?.let { (it as DrawerActivity).setupPurchasedIcons() }
                }
                Log.w(TAG, "Error purchasing: " + result)
            } else {
                purchases.forEach { _, purchaseStructDto ->
                    if (purchaseStructDto.purchaseId == purchase.sku) {
                        purchaseStructDto.hasPurchased = true
                    }
                }
                drawer?.let { (it as DrawerActivity).setupPurchasedIcons() }
            }
        })
    }

    interface SplashStartApp {
        fun startApp()
    }

    interface DrawerInterface {
        fun showPurchaseMessage()
    }
}