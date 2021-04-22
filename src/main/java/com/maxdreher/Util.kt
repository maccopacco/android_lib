package com.maxdreher

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.Button
import androidx.navigation.findNavController
import java.io.InputStream
import java.net.URL


/**
 * Utility class
 */
class Util {
    companion object {
        fun getDefaultMargin(context: Context?, id: Int): Int {
            return context!!.resources.getDimension(id).toInt()
        }

        /**
         * Convert map of buttons to navigate to specified resource ids
         */
        fun buttonToAction(view: View, map: Map<Int, Int>) {
            val navy = view.findNavController()
            map.entries.forEach { entry ->
                view.findViewById<Button>(entry.key).setOnClickListener {
                    navy.navigate(entry.value)
                }
            }
        }

        /**
         * Apply [View.OnClickListener] to map of buttons
         */
        fun buttonToListener(view: View, map: Map<Int, View.OnClickListener>) {
            map.entries.forEach { entry ->
                view.findViewById<View>(entry.key).setOnClickListener(entry.value)
            }
        }

        /**
         * Convert [uri] to [Drawable]
         */
        fun uriToDrawable(uri: Uri?): Drawable? {
            return uri?.let {
                urlToDrawable(it.path)
            }
        }

        fun urlToDrawable(url: String?): Drawable? {
            return try {
                val `is`: InputStream = URL(url).content as InputStream
                Drawable.createFromStream(`is`, "src name")
            } catch (e: Exception) {
                null
            }
        }

    }

}