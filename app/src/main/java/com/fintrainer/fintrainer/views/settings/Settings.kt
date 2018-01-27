package com.fintrainer.fintrainer.views.settings

import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.view.MenuItem
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.utils.Constants.APP_PNAME
import com.fintrainer.fintrainer.utils.Constants.CLEAR_FAVOURITE_PREF
import com.fintrainer.fintrainer.utils.Constants.CLEAR_STATISTICS_PREF
import com.fintrainer.fintrainer.views.BaseActivity
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.alert

class Settings : BaseActivity() {

    private var examType: Int? = null
    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Настройки"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        examType = intent.getIntExtra("type", -1)

        if (savedInstanceState == null) {
            fragment = SettingsFragment()
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment, "settings").commit()
        } else {
            fragment = fragmentManager.findFragmentByTag("settings")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }

    class SettingsFragment : PreferenceFragment() {

        internal lateinit var clearFavourite: Preference
        internal lateinit var clearStatistics: Preference
        internal lateinit var like: Preference
        internal lateinit var info: Preference

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.main_prefs)

            clearFavourite = findPreference("key_clear_favourite")
            clearStatistics = findPreference("key_remove_statistics")
            info = findPreference("key_information")
            like = findPreference("key_like")

            clearFavourite.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                showDialog(getString(R.string.clear_favourite), getString(R.string.clear_favourite_dialog), CLEAR_FAVOURITE_PREF)
                true
            }
            clearStatistics.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                showDialog(getString(R.string.remove_statistics), getString(R.string.clear_statistic_dialog), CLEAR_STATISTICS_PREF)
                true
            }
            info.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //                showInfoDialog()
                true
            }
            like.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + APP_PNAME)))
                true
            }
        }

        private fun showDialog(title: String, message: String, type: Int) {
            val dialog = alert(message, title)
            dialog.positiveButton(getString(R.string.yes)) {}
            dialog.negativeButton(getString(R.string.no)) {}
            dialog.show()
        }
    }
}
