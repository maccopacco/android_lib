package com.maxdreher.amphelper

import com.amplifyframework.datastore.DataStoreException
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.function.Consumer

/**
 * Helper class for Amazon's Amplify DataStore caslls
 *
 * Holds
 * consumers for exception (consumer for data must be provided)
 * helper methods to wait until data accepted
 */
open class AmpHelperBase<ReturnType> {
    protected var data: ReturnType? = null
    private var exception: DataStoreException? = null

    /**
     * Consumer which resets old data on access and returns a consumer which
     * sets the local [exception] value
     */
    val b: (value: DataStoreException) -> Unit
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
     * Handle success / fail logic
     */
    fun afterWait(
        onSuccess: Consumer<ReturnType>,
        onFail: Consumer<DataStoreException>,
    ) {
        loop {
            data?.let { onSuccess.accept(it) } ?: exception?.let { onFail.accept(it) }
        }
    }

    /**
     * Loop while waiting for data or exception, then run [Runnable] with
     * main scope
     */
    private fun loop(
        afterwards: Runnable,
    ) {
        GlobalScope.launch {
            while (data == null && exception == null) {
                delay(50L)
            }
            withContext(Dispatchers.Main) {
                afterwards.run()
            }
        }
    }
}