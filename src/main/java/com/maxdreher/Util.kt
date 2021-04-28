package com.maxdreher

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.Button
import androidx.navigation.findNavController
import com.maxdreher.extensions.IContextBase
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


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
        fun buttonToListener(view: View, map: Map<Int, () -> Unit>) {
            map.entries.forEach { entry ->
                view.findViewById<View>(entry.key).setOnClickListener { entry.value.invoke() }
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

        /**
         * Convert [url] to [Drawable]
         */
        fun urlToDrawable(url: String?): Drawable? {
            return try {
                val `is`: InputStream = URL(url).content as InputStream
                Drawable.createFromStream(`is`, "src name")
            } catch (e: Exception) {
                null
            }
        }

        /**
         * Get random [Int] between [min] and [max]
         */
        fun getRandInt(min: Int, max: Int): Int {
            return min + (Math.random() * (max - min + 1)).toInt()
        }

        fun getSaneDate(date: Date = Date()): String {
            return SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS").format(date)
        }

        fun startLogging(cb: IContextBase): Boolean {
            val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(Date())
            try {
                val dir = "/data/data/${cb.getContext()?.applicationInfo?.packageName}/logs"
                Runtime.getRuntime()
                    .exec("mkdir $dir")
                Runtime.getRuntime().exec("logcat -f $dir/$date.txt")
                cb.log("Started logging")
                return true
            } catch (e: IOException) {
                cb.loge("Could not start logging")
            }
            return false
        }
    }

}