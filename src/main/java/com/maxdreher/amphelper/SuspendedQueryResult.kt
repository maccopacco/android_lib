package com.maxdreher.amphelper

import com.amplifyframework.core.model.Model
import com.amplifyframework.datastore.DataStoreException

class SuspendedQueryResult<T : Model>(
    val result: List<T>? = null,
    val exception: DataStoreException? = null
) {

    constructor(exception: DataStoreException) : this(null, exception)

    fun with(
        onSuccess: (List<T>) -> Unit,
        onFailure: (DataStoreException) -> Unit
    ) {
        result?.let(onSuccess) ?: exception?.let(onFailure)
    }

    fun getOr(onFailure: (DataStoreException) -> Unit): List<T>? {
        return result ?: let {
            exception?.let(onFailure)
            null
        }
    }
}