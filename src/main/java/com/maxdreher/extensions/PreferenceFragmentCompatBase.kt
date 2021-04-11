package com.maxdreher.extensions

import android.os.Bundle
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragmentCompat

open class PreferenceFragmentCompatBase(@XmlRes private val preferencesResId: Int) :
    PreferenceFragmentCompat(), IContextBase {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(preferencesResId, rootKey)
    }
}