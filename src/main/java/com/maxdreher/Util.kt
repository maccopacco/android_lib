package com.maxdreher

import android.content.Context

class Util {
    companion object {
        fun getDefaultMargin(context: Context, id: Int): Int {
            with(context) {
                return resources.getDimension(id).toInt()
            }
        }
    }

}