package com.chenguang.simpleclock.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.chenguang.simpleclock.R

/**
 * Fragment to display root setting options
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}
