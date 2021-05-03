package com.maxdreher.extensions

import android.os.Bundle
import androidx.annotation.XmlRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

open class PreferenceFragmentCompatBase(@XmlRes private val preferencesResId: Int) :
    PreferenceFragmentCompat(), IContextBase {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(preferencesResId, rootKey)
    }

    fun findPreference(name: String, onClick: () -> Unit = {}): Preference? {
        return findPreferenceReturn(name) { onClick.invoke();true }
    }

    fun findPreferenceReturn(name: String, onClick: () -> Boolean): Preference? {
        return findPreference<Preference>(name)?.apply {
            setOnPreferenceClickListener {
                log("Preference $name clicked")
                return@setOnPreferenceClickListener onClick.invoke()
            }
        } ?: let {
            loge("Preference [$name] could not be found")
            null
        }
    }
}