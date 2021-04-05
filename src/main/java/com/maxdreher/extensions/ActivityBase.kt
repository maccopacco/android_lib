package com.maxdreher.extensions

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Implementation of [IContextBase] for [AppCompatActivity]
 */
open class ActivityBase(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId),
    IContextBase {
    override fun getContext(): Context? = applicationContext
}