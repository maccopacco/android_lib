package com.maxdreher.extensions

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Implementation of [IContextBase] for [AppCompatActivity]
 */
open class ActivityBase(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId),
    IContextBase {
    override fun getContext(): Context? = applicationContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreated()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        onCreated()
    }

    private fun onCreated() {
        log("Created")
    }
}