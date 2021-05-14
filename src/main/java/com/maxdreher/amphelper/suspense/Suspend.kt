package com.maxdreher.amphelper.suspense

import com.amplifyframework.datastore.DataStoreException

open class Suspend<ReturnType>(
    val result: ReturnType? = null,
    val exception: DataStoreException? = null
) {
    constructor(exception: DataStoreException) : this(null, exception)

    fun with(
        onSuccess: (ReturnType) -> Unit,
        onFailure: (DataStoreException) -> Unit
    ) {
        result?.let(onSuccess) ?: exception?.let(onFailure)
    }

    fun getOr(onFailure: (DataStoreException) -> Unit): ReturnType? {
        return result ?: let {
            exception?.let(onFailure)
            null
        }
    }
}