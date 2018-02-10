package com.fintrainer.fintrainer.views.settings

import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.view.MenuItem
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.structure.AverageGradeStatisticDto
import com.fintrainer.fintrainer.utils.Constants.APP_PNAME
import com.fintrainer.fintrainer.utils.Constants.CLEAR_FAVOURITE_PREF
import com.fintrainer.fintrainer.utils.Constants.CLEAR_STATISTICS_PREF
import com.fintrainer.fintrainer.utils.Constants.EXAM_BASE
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_1
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_2
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_3
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_4
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_5
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_6
import com.fintrainer.fintrainer.utils.Constants.EXAM_SERIAL_7
import com.fintrainer.fintrainer.utils.containers.RealmContainer
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseActivity
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import javax.inject.Inject

class Settings : BaseActivity() {

    private var examType: Int? = null
    private var fragment: Fragment? = null

    @Inject
    lateinit var realmContainer: RealmContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)
        App.appComponent.inject(this)

        println("Container $realmContainer")

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
//        internal lateinit var like: Preference
//        internal lateinit var info: Preference

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.main_prefs)

            clearFavourite = findPreference("key_clear_favourite")
            clearStatistics = findPreference("key_remove_statistics")
//            info = findPreference("key_information")
//            like = findPreference("key_like")

            clearFavourite.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                showDialog(getString(R.string.clear_favourite), getString(R.string.clear_favourite_dialog) + examTypePrefix(), CLEAR_FAVOURITE_PREF)
                true
            }
            clearStatistics.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                showDialog(getString(R.string.remove_statistics), getString(R.string.clear_statistic_dialog) + examTypePrefix(), CLEAR_STATISTICS_PREF)
                true
            }
        }

        private fun examTypePrefix() = when ((activity as Settings).examType) {
            EXAM_BASE -> {
                "\"Базового экзамена\""
            }
            EXAM_SERIAL_1 -> {
                "экзамена \"Первой серии\""
            }
            EXAM_SERIAL_2 -> {
                "экзамена \"Воторой серии\""
            }
            EXAM_SERIAL_3 -> {
                "экзамена \"Третьей серии\""
            }
            EXAM_SERIAL_4 -> {
                "экзамена \"Четвертой серии\""
            }
            EXAM_SERIAL_5 -> {
                "экзамена \"Пятой серии\""
            }
            EXAM_SERIAL_6 -> {
                "экзамена \"Шестой серии\""
            }
            EXAM_SERIAL_7 -> {
                "экзамена \"Седьмой серии\""
            }
            else -> {
                ""
            }
        }

        private fun showDialog(title: String, message: String, type: Int) {
            val dialog = alert(message, title)
            dialog.positiveButton(getString(R.string.yes)) {
                if (type == CLEAR_STATISTICS_PREF) {
                    (activity as Settings).clearStat()
                }
                if (type == CLEAR_FAVOURITE_PREF) {
                    (activity as Settings).clearFav()
                }
            }
            dialog.negativeButton(getString(R.string.no)) {}
            dialog.show()
        }
    }

    private fun clearStat() {
        examType?.let {
            toast(realmContainer.clearStatistics(it))
        }
    }

    private fun clearFav() {
        examType?.let { toast(realmContainer.clearFavourite(it)) }
    }
}
