package com.maxdreher.amphelper

import com.amplifyframework.core.model.Model

class AmpHelperQ<ReturnType : Model> : AmpHelperBase<List<ReturnType>>() {
    val g: (MutableIterator<ReturnType>) -> Unit = {
        data = it.asSequence().toList()
    }
}