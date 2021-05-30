package com.maxdreher.extensions

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import androidx.annotation.LayoutRes
import java.lang.Exception

/**
 * A base which provides helper functions in exchange for [Context]
 */
interface IContextBase {
    fun getContext(): Context?

    val className: String
        get() = javaClass.simpleName

    fun loge(text: String) {
        log(text, error = true)
    }

    fun log(text: String, error: Boolean = false) {
        if (error) {
            Log.e(className, text)
        } else {
            Log.i(className, text)
        }
    }

    fun etoast(text: String) {
        toast(text, error = true)
    }

    fun toast(text: String, long: Boolean = false, error: Boolean = false) {
        log(text, error)
        Toast.makeText(
            getContext(),
            text,
            if (long || error) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).show()
    }

    fun alertBuilder(title: String, message: String? = null): AlertDialog.Builder {
        return AlertDialog.Builder(getContext()).setTitle(title).setMessage(message)
            .setPositiveButton("Ok") { _, _ -> }
    }

    fun alert(title: String, message: String, error: Boolean = false) {
        log(message, error)
        try {
            alertBuilder(title, message).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun error(message: String) {
        alert("Error", message, true)
    }

    fun call(param: Any): String? {
        return param.javaClass.enclosingMethod?.name.also {
            log("Called $it")
        }
    }


}