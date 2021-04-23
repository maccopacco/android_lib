package com.maxdreher.amphelper

import com.amplifyframework.core.model.Model
import com.amplifyframework.datastore.DataStoreItemChange

/**
 * [AmpHelperBase] for modifications
 */
class AmpHelper<ReturnType : Model> : AmpHelperDataStoreBase<ReturnType>() {
    val g: (value: DataStoreItemChange<ReturnType>) -> Unit = { data = it.item() }
}