package com.maxdreher.amphelper

import com.amplifyframework.core.Action
import com.amplifyframework.core.model.Model
import com.amplifyframework.datastore.DataStoreException
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.function.Consumer

open class AmpHelperBase<ReturnType> {
    protected var data: ReturnType? = null
    private var exception: DataStoreException? = null

    val b: (value: DataStoreException) -> Unit
        get() = reset().let {
            { exception = it }
        }

    private fun reset() {
        data = null
        exception = null
    }

    fun afterWait(
        onSuccess: Consumer<ReturnType>,
        onFail: Consumer<DataStoreException>,
    ) {
        loop {
            data?.let { onSuccess.accept(it) } ?: exception?.let { onFail.accept(it) }
        }
    }

    private fun loop(
        run: Runnable,
    ) {
        GlobalScope.launch {
            while (data == null && exception == null) {
                delay(50L)
            }
            withContext(Dispatchers.Main) {
                run.run()
            }
        }
    }
}