package com.chenguang.simpleclock.setting

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chenguang.simpleclock.BuildConfig
import com.chenguang.simpleclock.R

/**
 * Fragment to display app basic information such as version and copyright
 */
class AboutAppFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.about_app, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findPreference<Preference>(getString(R.string.app_version_setting_key))?.summary =
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }
}
