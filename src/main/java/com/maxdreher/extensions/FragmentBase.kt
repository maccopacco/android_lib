package com.maxdreher.extensions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Implementation of [IContextBase] for [Fragment]
 */
open class FragmentBase<Type : ViewBinding>(
    private val `class`: Class<Type>,
) :
    Fragment(),
    IContextBase {

    private var _binding: Type? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("On create")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        @Suppress("UNCHECKED_CAST")
        _binding =
            `class`.getMethod(
                "inflate", LayoutInflater::class.java,
                ViewGroup::class.java, Boolean::class.java
            ).invoke(null, inflater, container, false) as Type?
        return binding.root
    }

}