package com.maxdreher.amphelper

import com.amplifyframework.core.model.Model

/**
 * [AmpHelperBase] for queries, which returns type as a list
 */
class AmpHelperQ<ReturnType : Model> : AmpHelperBase<List<ReturnType>>() {
    val g: (MutableIterator<ReturnType>) -> Unit = {
        data = it.asSequence().toList()
    }
}