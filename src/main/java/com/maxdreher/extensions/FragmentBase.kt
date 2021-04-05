package com.maxdreher.extensions

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * Implementation of [IContextBase] for [Fragment]
 */
open class FragmentBase(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId), IContextBase