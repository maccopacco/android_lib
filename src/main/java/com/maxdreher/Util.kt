package com.maxdreher

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.navigation.findNavController
import java.lang.Exception

class Util {
    companion object {
        fun getDefaultMargin(context: Context?, id: Int): Int {
            return context!!.resources.getDimension(id).toInt()
        }

        fun buttonToAction(view: View, map: Map<Int, Int>) {
            val navy = view.findNavController()
            map.entries.forEach { entry ->
                view.findViewById<Button>(entry.key).setOnClickListener {
                    navy.navigate(entry.value)
                }
            }
        }

        fun buttonToListener(view: View, map: Map<Int, View.OnClickListener>) {
            map.entries.forEach { entry ->
                view.findViewById<View>(entry.key).setOnClickListener(entry.value)
            }
        }
    }

}