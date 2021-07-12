package com.maxdreher.extensions

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.maxdreher.Util
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implementation of [IContextBase] for [AppCompatActivity]
 */
open class ActivityBase<Type : ViewBinding>(private val `class`: Class<Type>) :
    AppCompatActivity(),
    IContextBase {

    private var _binding: Type? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    val binding get() = _binding!!

    companion object {
        private val hasStartedLogging = AtomicBoolean(false)
    }

    override fun getContext(): Context? = this@ActivityBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = Util.reflectInflate(`class`, layoutInflater)

        onCreated()
        hasStartedLogging.run {
            if (!get()) {
                set(Util.startLogging(this@ActivityBase))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        onCreated()
    }

    private fun onCreated() {
        log("Created")
    }
}