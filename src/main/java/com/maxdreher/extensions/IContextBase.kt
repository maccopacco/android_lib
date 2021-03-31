package com.maxdreher.extensions

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.LayoutRes

interface IContextBase {
    fun getContext(): Context?

    fun getClassName(): String {
        return javaClass.simpleName
    }

    fun log(text: String) {
        Log.i(getClassName(), text)
    }

    fun toast(text: String) {
        log(text)
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show()
    }
}