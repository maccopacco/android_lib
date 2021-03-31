package com.maxdreher.amphelper

import com.amplifyframework.core.model.Model
import com.amplifyframework.datastore.DataStoreItemChange

class AmpHelper<ReturnType : Model> : AmpHelperBase<ReturnType>() {
    val g: (value: DataStoreItemChange<ReturnType>) -> Unit = { data = it.item() }
}