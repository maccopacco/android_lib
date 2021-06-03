package com.maxdreher.extensions

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.lang.Exception
import kotlin.system.measureTimeMillis

/**
 * A base which provides helper functions in exchange for [Context]
 */
interface IContextBase {
    fun getContext(): Context?

    val className: String
        get() = javaClass.simpleName

    suspend fun <T> timeSuspend(message: String, block: suspend () -> T): T {
        val result: T
        val time = measureTimeMillis {
            result = block()
        }
        logTime(time, message)
        return result
    }

    fun <T> time(message: String, block: () -> T): T {
        val result: T
        val time = measureTimeMillis {
            result = block()
        }
        logTime(time, message)
        return result
    }

    fun logTime(time: Long, message: String) {
        log("Took ${time}ms to $message", error = false, append = "/Timing")
    }

    fun loge(text: String) {
        log(text, error = true)
    }

    fun log(text: String, error: Boolean = false, append: String = "") {
        val tag = "$className$append"
        if (error) {
            Log.e(tag, text)
        } else {
            Log.i(tag, text)
        }
    }

    fun etoast(text: String) {
        toast(text, error = true)
    }

    fun toast(text: String, long: Boolean = false, error: Boolean = false) {
        log(text, error = error)
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
        log(message, error = error)
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
        return getCaller(param).also {
            log("Called $it")
        }
    }

    fun getCaller(param: Any) = param.javaClass.enclosingMethod?.name

    fun getCallerSafe(param: Any) = getCaller(param) ?: "null"
}