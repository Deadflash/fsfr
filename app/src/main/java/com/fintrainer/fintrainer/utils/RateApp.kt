package com.fintrainer.fintrainer.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.utils.Constants.APP_PNAME
import com.fintrainer.fintrainer.utils.Constants.DAYS_UNTIL_PROMPT
import com.fintrainer.fintrainer.utils.Constants.LAUNCHES_UNTIL_PROMPT
import org.jetbrains.anko.alert

/**
 * Created by krotk on 12.02.2018.
 */
class RateApp {

    companion object {

        @JvmStatic
        private var launch_count = 0L

        fun setupRate(context: Context) {
            val prefs = context.getSharedPreferences("rate", 0)
            if (prefs.getBoolean("dontshowagain", false)) {
                return
            }

            val editor = prefs.edit()

            // Increment launch counter
            launch_count = prefs.getLong("launch_count", 0) + 1
            editor.putLong("launch_count", launch_count)

            // Get date of first launch
            var dateFirstLaunch: Long = prefs.getLong("date_firstlaunch", 0L)
            if (dateFirstLaunch == 0L) {
                dateFirstLaunch = System.currentTimeMillis()
                editor.putLong("date_firstlaunch", dateFirstLaunch)
            }
            // Wait at least n days before opening
            editor.apply()
        }

        fun showRateDialog(activity: Activity) {
            if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
                val prefs = activity.getSharedPreferences("rate", 0)
                val dateFirstLaunch: Long = prefs.getLong("date_firstlaunch", 0L)
                val editor =  prefs.edit()
                if (System.currentTimeMillis() >= dateFirstLaunch + DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000) {
//                if (true) {
                    val dialog = activity.alert(activity.getString(R.string.rateDescription), activity.getString(R.string.rateApp))
                    dialog.positiveButton(activity.getString(R.string.rate)) {
                        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.play_store_url) + APP_PNAME)))
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true)
                            editor.apply()
                        }
                    }
                    dialog.negativeButton(activity.getString(R.string.later)) {
                        if (editor != null) {
                            editor.putLong("launch_count", 0)
                            launch_count = 0
                            editor.apply()
                        }
                    }
                    dialog.show()
                }
                editor.apply()
            }
        }
    }
}