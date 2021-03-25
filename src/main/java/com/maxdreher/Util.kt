package com.maxdreher

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.navigation.findNavController

class Util {
    companion object {
        fun getDefaultMargin(context: Context, id: Int): Int {
            with(context) {
                return resources.getDimension(id).toInt()
            }
        }

        fun buttonToAction(view: View, map: Map<Int, Int>) {
            val navy = view.findNavController()
            map.entries.forEach { entry ->
                view.findViewById<Button>(entry.key).setOnClickListener {
                    navy.navigate(entry.value)
                }
            }
        }
    }

}