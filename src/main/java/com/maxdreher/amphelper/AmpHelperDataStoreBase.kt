package com.maxdreher.amphelper

import com.amplifyframework.datastore.DataStoreException

open class AmpHelperDataStoreBase<ReturnType> : AmpHelperBase<ReturnType, DataStoreException>() {
    override suspend fun onTimeout(onFail: suspend (DataStoreException) -> Unit, timeout: Int) {
        onFail(DataStoreException("Timed out (${timeout} ms)", "Try again"))
    }
}