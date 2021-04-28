package com.maxdreher.extensions

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.maxdreher.Util
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implementation of [IContextBase] for [AppCompatActivity]
 */
open class ActivityBase(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId),
    IContextBase {

    companion object {
        private val hasStartedLogging = AtomicBoolean(false)
    }

    override fun getContext(): Context? = applicationContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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