package com.maxdreher.amphelper

import com.amplifyframework.core.Action

class AmpHelperD : AmpHelperBase<String>() {
    val g = Action {
        data = "not null"
    }
}