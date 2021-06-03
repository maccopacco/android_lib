package com.maxdreher

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import androidx.navigation.findNavController
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.QueryOptions
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.query.predicate.QueryField
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.core.model.query.predicate.QueryPredicates
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.DataStoreException
import com.maxdreher.amphelper.AmpHelper
import com.maxdreher.amphelper.AmpHelperD
import com.maxdreher.amphelper.AmpHelperQ
import com.maxdreher.amphelper.suspense.Suspend
import com.maxdreher.amphelper.suspense.SuspendQuery
import com.maxdreher.extensions.IContextBase
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.lang.Integer.max
import java.lang.Integer.min
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass


/**
 * Utility class
 */
object Util {

    object Date {
        private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS")

        fun java.util.Date.toAmplifyDate(): Temporal.Date {
            return Temporal.Date(this)
        }

        fun java.util.Date.toAmplifyDateTime(): Temporal.DateTime {
            return Temporal.DateTime(this, 0)
        }

        fun getDateTime(): String {
            return Date().toDateTime()
        }

        fun java.util.Date.toSimpleDate(): String {
            return simpleDateFormat.format(this)
        }

        fun String.toSimpleDate(): java.util.Date? {
            return try {
                simpleDateFormat.parse(this)
            } catch (e: Exception) {
                null
            }
        }

        fun java.util.Date.toDateTime(): String {
            return dateTimeFormat.format(this)
        }

        fun String.toDateTime(): java.util.Date? {
            return try {
                dateTimeFormat.parse(this)
            } catch (e: Exception) {
                null
            }
        }
    }

    inline fun <T> Iterable<T>.onlyOne(predicate: (T) -> Boolean): Boolean {
        var oneFound = false
        forEach { item ->
            if (item.let(predicate)) {
                if (oneFound) {
                    return false
                }
                oneFound = true
            }
        }
        return oneFound
    }

    fun getDefaultMargin(context: Context?, id: Int): Int {
        return context!!.resources.getDimension(id).toInt()
    }

    fun Throwable.get(): String? {
        printStackTrace()
        return message
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
    fun View.buttonToListener(vararg items: Pair<Int, () -> Unit>) {
        items.forEach { entry ->
            findViewById<View>(entry.first).setOnClickListener { entry.second.invoke() }
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


    fun <T> List<T>.safeSublist(fromIndex: Int, toIndex: Int): List<T> {
        val from = max(min(size - 1, fromIndex), 0)
        val to = max(min(size - 1, toIndex), 0)
        if (from > to) {
            return emptyList()
        }
        return subList(from, to)
    }

    fun View.setMargin(wrapHorizontal: Boolean, wrapVertical: Boolean, margin: Int) {
        layoutParams =
            LinearLayout.LayoutParams(wrapHorizontal.wrapToInt(), wrapVertical.wrapToInt())
                .apply {
                    setMargins(margin, margin, margin, margin)
                }
    }

    fun ViewGroup.setMargin(wrapHorizontal: Boolean, wrapVertical: Boolean, margin: Int) {
        layoutParams =
            ViewGroup.MarginLayoutParams(wrapHorizontal.wrapToInt(), wrapVertical.wrapToInt())
                .apply {
                    setMargins(margin, margin, margin, margin)
                }
    }

    private fun Boolean.wrapToInt(): Int {
        return if (this) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    fun DatePicker.getDate(): java.util.Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return calendar.time
    }
}
