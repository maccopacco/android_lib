package com.maxdreher.amphelper

import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.Runnable

/**
 * Helper class for Amazon's Amplify DataStore caslls
 *
 * Holds
 * consumers for exception (consumer for data must be provided)
 * helper methods to wait until data accepted
 */
abstract class AmpHelperBase<ReturnType, ExceptionType : Exception> {
    protected var data: ReturnType? = null
    private var exception: ExceptionType? = null

    /**
     * Consumer which resets old data on access and returns a consumer which
     * sets the local [exception] value
     */
    val b: (value: ExceptionType) -> Unit
        get() = reset().let {
            { exception = it }
        }

    /**
     * Resets data + exception
     */
    private fun reset() {
        data = null
        exception = null
    }


    /**
     * Loop while waiting for data or exception, then run [Runnable] with
     * main scope
     */
    fun afterWait(
        onSuccess: (ReturnType) -> Unit,
        onFail: (ExceptionType) -> Unit,
    ) {
        GlobalScope.launch {
            afterWaitSuspense({
                onSuccess.invoke(it)
            }, {
                onFail.invoke(it)
            })
        }
    }

    suspend fun afterWaitSuspense(
        onSuccess: suspend (ReturnType) -> Unit,
        onFail: suspend (ExceptionType) -> Unit,
        timeoutMilliseconds: Int = 5000
    ) {
        val startTime = System.currentTimeMillis()
        var didTimeout = false
        val isTimedOut = {
            (System.currentTimeMillis() - startTime > timeoutMilliseconds).also {
                if (it)
                    didTimeout = true
            }
        }
        while (data == null && exception == null && !isTimedOut.invoke()) {
            delay(50L)
        }
        if (didTimeout) {
            println("afterWaitSuspense Timed out")
            onTimeout(onFail, timeoutMilliseconds)
        } else {
            withContext(Dispatchers.Main) {
                data?.let { onSuccess.invoke(it) } ?: exception?.let { onFail.invoke(it) }
            }
        }
    }

    abstract suspend fun onTimeout(onFail: suspend (ExceptionType) -> Unit, timeout: Int)
}