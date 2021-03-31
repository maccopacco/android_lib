package com.maxdreher.extensions

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

open class ActivityBase : AppCompatActivity(), IContextBase {
    override fun getContext(): Context? = applicationContext
}