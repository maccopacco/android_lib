package com.maxdreher.amphelper

import com.amplifyframework.core.Action

/**
 * [AmpHelperBase] for deletions
 */
class AmpHelperD : AmpHelperDataStoreBase<String>() {
    val g = Action {
        data = "not null"
    }
}