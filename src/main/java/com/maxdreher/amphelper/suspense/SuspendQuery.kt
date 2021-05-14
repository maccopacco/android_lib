package com.maxdreher.amphelper.suspense

import com.amplifyframework.datastore.DataStoreException

class SuspendQuery<ReturnType>(
    result: List<ReturnType>? = null,
    exception: DataStoreException? = null
) :
    Suspend<List<ReturnType>>(result, exception) {
    constructor(exception: DataStoreException) : this(null, exception)
}