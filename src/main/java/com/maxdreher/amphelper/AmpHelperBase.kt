package com.maxdreher.amphelper

import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.Runnable
import java.util.function.Consumer

/**
 * Helper class for Amazon's Amplify DataStore caslls
 *
 * Holds
 * consumers for exception (consumer for data must be provided)
 * helper methods to wait until data accepted
 */
open class AmpHelperBase<ReturnType, ExceptionType : Exception> {
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
    ) {
        while (data == null && exception == null) {
            delay(50L)
        }
        withContext(Dispatchers.Main) {
            data?.let { onSuccess.invoke(it) } ?: exception?.let { onFail.invoke(it) }
        }
    }
}