package com.maxdreher.extensions

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * Implementation of [IContextBase] for [Fragment]
 */
open class FragmentBase(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId), IContextBase {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("On create")
    }
}