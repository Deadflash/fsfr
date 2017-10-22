package com.fcpunlimited.fsfr.views

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fcpunlimited.fsfr.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by krotk on 21.10.2017.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoMedium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}